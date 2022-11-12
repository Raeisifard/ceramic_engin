package com.ceramic.api;

import com.ceramic.shared.ShareableRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.healthchecks.HealthChecks;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

public class EventBusBridgeVerticle extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(EventBusBridgeVerticle.class);
    private static HealthChecks healthChecks;

    @Override
    public void start(Promise<Void> startPromise) {
        log.info("Starting verticle {" + this + "}");
        healthChecks = HealthChecks.create(vertx);
        vertx.eventBus().consumer("eventbus.bridge.verticle.health",
                message -> healthChecks.checkStatus()
                        .onSuccess(res -> message.reply("OK"))
                        .onFailure(err -> message.fail(0, err.getMessage())));

        String inboundAddress = config().getString("inboundAddress");
        String outboundAddress = config().getString("outboundAddress");
        HttpServer server = vertx.createHttpServer(new HttpServerOptions().setCompressionSupported(true));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        PermittedOptions inboundPermitted = new PermittedOptions().setAddress(inboundAddress);
        PermittedOptions outboundPermitted = new PermittedOptions().setAddress(outboundAddress);
        SockJSBridgeOptions options = new SockJSBridgeOptions()
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("^vx\\.mx(\\.\\w+)+"))
                .addInboundPermitted(new PermittedOptions().setAddress("mx.vx"))
                .addInboundPermitted(new PermittedOptions().setAddress("gateway"))
                .addInboundPermitted(new PermittedOptions().setAddress("test01"))
                .addInboundPermitted(new PermittedOptions().setAddress("test02"))
                .addInboundPermitted(new PermittedOptions().setAddress("test03"))
                .addOutboundPermitted(new PermittedOptions().setAddress("vx.mx"))
                .addInboundPermitted(new PermittedOptions().setAddressRegex("mx.vx\\..+"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("vx.mx\\..+"))
                .addInboundPermitted(inboundPermitted)
                .addOutboundPermitted(outboundPermitted);

        final Router main = ShareableRouter.router(vertx);
        main.mountSubRouter("/eventbus", sockJSHandler.bridge(options));
        // start server
        server.requestHandler(main).listen(config().getInteger("http-port"), lh -> {
            if (lh.failed()) {
                System.out.println("Eventbus bridge failed");
                startPromise.fail(lh.cause());
            } else {
                startPromise.complete();
            }
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) {
    }
}
