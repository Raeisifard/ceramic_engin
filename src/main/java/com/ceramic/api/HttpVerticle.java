package com.ceramic.api;

import com.ceramic.shared.ShareableRouter;
import com.vx6.utils.Gzip;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.StaticHandler;

import java.util.Date;
import java.util.UUID;

public class HttpVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(HttpVerticle.class);
    private final StaticHandler staticFiles = StaticHandler.create();
    private final Router router = Router.router(vertx);
    private HttpServer server;
    private EventBus eb;
    private LocalMap<String, JsonObject> graphMap;
    private Long purgeId;

    @Override
    public void start(Promise<Void> startPromise) {
        this.eb = vertx.eventBus();
        SharedData sharedData = vertx.sharedData();
        graphMap = sharedData.getLocalMap("_FORM_GRAPH_MAP");
        log.info("Starting verticle {" + this + "}");
        purgeId = vertx.setPeriodic(300000, id -> {
            if (!graphMap.isEmpty()) {
                for (String key : graphMap.keySet()) {
                    if (graphMap.get(key).containsKey("time")) {
                        Date time = Date.from(graphMap.get(key).getInstant("time"));
                        if (new Date().getTime() - time.getTime() > 300000) {
                            graphMap.remove(key);
                            //System.out.println("graphmap key: " + key + " have been removed!");
                            //System.out.println(new Date() + " graphmap.size: " + graphMap.size());
                        }
                    } else {
                        graphMap.put(key, graphMap.get(key).put("time", new Date().toInstant()));
                    }
                }
            }
        });
        //router.get("/*").handler(staticFiles);
        router.post("/form").handler(BodyHandler.create()/*.setMergeFormAttributes(true)*/);
        // enable CORS
        router.options("/form").handler(CorsHandler.create("*")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowCredentials(true)
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Authorization")
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type")
                .allowedHeader("Content-Encoding"));
        router.post("/form").handler(CorsHandler.create("*")
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowCredentials(true)
                .allowedHeader("Access-Control-Allow-Headers")
                .allowedHeader("Authorization")
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Credentials")
                .allowedHeader("Content-Type")
                .allowedHeader("Content-Encoding"));

        router.post("/form").handler(ctx -> {
            //ctx.request().setExpectMultipart(true);
            ctx.next();
        }).blockingHandler(ctx -> {
            JsonObject body;
            try {
                body = ctx.getBodyAsJson();
                if (body.containsKey("graph")) {
                    body.put("graph", Gzip.decompress(body.getString("graph").getBytes()));
                    body.put("zip", false);
                }
            } catch (Exception e) {
                try {
                    body = new JsonObject(Gzip.decompress(ctx.getBody().getBytes()));
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            body.put("time", new Date().toInstant());
            UUID uuid = UUID.randomUUID();
            if (body.getString("cmd").equalsIgnoreCase("deploy")) {
                graphMap.put(uuid.toString(), body);
                JsonObject finalBody = body;
                eb.request("mx.vx", body.copy().put("graph", uuid.toString()), new DeliveryOptions(), ar -> {
                    if (ar.succeeded()) {
                        ctx.response().end(ar.result().toString());
                    } else {
                        ctx.response().end("failed");
                    }
                });
            } else if (body.getString("cmd").equalsIgnoreCase("redeploy")) {
                graphMap.put(uuid.toString(), body);
                JsonObject finalBody1 = body;
                eb.request("mx.vx", body.copy().put("graph", uuid.toString()), new DeliveryOptions(), ar -> {
                    if (ar.succeeded()) {
                        ctx.response().end(ar.result().toString());
                    } else {
                        ctx.response().end("failed");
                    }
                });
            } else if (body.getString("cmd").equalsIgnoreCase("view")) {
                this.eb.request("registry",
                        body.copy(), reg -> {
                            if (reg.succeeded()) {
                                JsonObject regBody = (JsonObject) reg.result().body();
                                ctx.response().end((regBody.put("graph", graphMap.get(regBody.getString("graph")).getString("graph"))).toString());
                            } else
                                ctx.response().end(reg.cause().getMessage());
                        });
            } else if (body.getString("cmd").equalsIgnoreCase("save")) {
                graphMap.put(uuid.toString(), body);
                eb.request("registry", body.copy().put("graph", uuid.toString()), new DeliveryOptions(), ar -> {
                    if (ar.succeeded()) {
                        ctx.response().end(ar.result().body().toString());
                    } else {
                        ctx.response().end("failed");
                    }
                });
            } else
                ctx.response().end(body.copy().toString());
        });
        router.get("/*").handler(staticFiles);
        //mount the router as subrouter to the shared router
        final Router main = ShareableRouter.router(vertx);
        main.mountSubRouter("/stt", router);
        //Create landing page.
        main.get("/").handler(rc -> rc.response().setStatusCode(302).putHeader("location", config().getString("location")).end());
        server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true)).requestHandler(main).listen(config().getInteger("http-port"), res -> {
            if (res.succeeded()) {
                startPromise.complete();
            } else {
                startPromise.fail(res.cause());
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
        if (this.server != null) {
            this.server.close();
        }
        router.clear();
        vertx.cancelTimer(purgeId);
        stopPromise.complete();
    }
}
