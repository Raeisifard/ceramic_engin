package com.ceramic.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

public class HttpStaticVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(HttpStaticVerticle.class);
    //private final StaticHandler staticFiles = StaticHandler.create();
    private final Router router = Router.router(vertx);
    private HttpServer server;

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Starting verticle {" + this + "}");
        //router.route().handler(BodyHandler.create());
        router.get("/*").handler(StaticHandler.create());

        server = vertx.createHttpServer(/*new HttpServerOptions().setCompressionSupported(true)*/)
                .requestHandler(router).listen(config().getInteger("http-port"), res -> {
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
        stopPromise.complete();
    }
}
