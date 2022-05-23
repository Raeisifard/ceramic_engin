package com.ceramic.api;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SyncVerticle extends AbstractVerticle {
    private Long timerId;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.timerId = vertx.setPeriodic(5000, new Handler<Long>() {
            @Override
            public void handle(Long aLong) {
                JsonObject sync = new JsonObject();
                sync.put("type", "sync").put("time", new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
                vertx.eventBus().publish("vx.mx", sync);
            }
        });

        startPromise.complete();
    }

    @Override
    public void stop(Promise<Void> closePromise) {
        if (this.timerId != null) {
            vertx.cancelTimer(this.timerId);
            this.timerId = null;
        }
        closePromise.complete();
    }
}
