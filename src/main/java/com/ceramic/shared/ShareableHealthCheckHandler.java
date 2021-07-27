package com.ceramic.shared;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.Shareable;
import io.vertx.ext.auth.authentication.AuthenticationProvider;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.impl.HealthCheckHandlerImpl;

public class ShareableHealthCheckHandler extends HealthCheckHandlerImpl implements Shareable {

    public static HealthCheckHandler create(Vertx vertx) {
        return (HealthCheckHandler) vertx.sharedData()
                .getLocalMap("health_check_handler")
                .computeIfAbsent("main", n -> new ShareableHealthCheckHandler(vertx));
    }

    ShareableHealthCheckHandler(final Vertx vertx) {
        super(vertx, (AuthenticationProvider)null);
    }
}
