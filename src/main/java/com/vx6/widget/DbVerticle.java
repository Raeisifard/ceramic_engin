package com.vx6.widget;

import com.ceramic.shared.ShareableHealthCheckHandler;
import com.vx6.master.MasterVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.healthchecks.Status;

public class DbVerticle extends MasterVerticle {
    long count = 0;
    private LocalMap<String, Long> healthMap;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        JsonObject data = config().getJsonObject("data");
        String dbType = data.getString("dbType", "SqlServer");
        JsonObject config = data.getJsonObject("config");
        Integer instance = config.getInteger("instance", 1);
        config().put("dbverticleid", deploymentID());
        DeploymentOptions options = new DeploymentOptions().setWorker(false);
        options.setConfig(config());
        vertx.deployVerticle("com.vx6.widget.db." + (inputConnected ? "write" : "read") + "." + dbType, options, result -> {
            if (result.succeeded()) {
                initPromise.complete();
            } else {
                initPromise.fail(result.result());
                result.cause().printStackTrace();
            }
        });
    }

    @Override
    public <T> void trigger(Message<T> tMessage) {
        if (tMessage.body().toString().equalsIgnoreCase("ready"))
            return;
        eb.send(deploymentID() + ".trigger", tMessage.body(), addressBook.getDeliveryOptions(tMessage));
    }

    @Override
    public <T> void input(Message<T> tMessage) {
        eb.send(deploymentID() + ".input", tMessage.body(), addressBook.getDeliveryOptions(tMessage));
    }

    @Override
    public <T> void process(Message<T> tMessage) {//Writing mode.
        eb.request(deploymentID(), tMessage.body(), ar -> {
            if (ar.succeeded()) {
                if (outputConnected) {
                    super.process(tMessage);
                    super.resultOutboundCount++;
                }
            } else {
                eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions()
                        .addHeader("error", "DB_WRITE_FAILED")
                        .addHeader("cause", ar.cause().getMessage()));
                super.errorOutboundCount++;
            }
        });
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        if (!inputConnected) {
            JsonObject jo = (JsonObject) tMessage.body();
            if (jo.containsKey("query")) {
                eb.request(deploymentID(), tMessage.body(), ar -> {
                    if (ar.succeeded()) {
                        super.process(tMessage);
                        super.resultOutboundCount++;
                    } else {
                        eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions()
                                .addHeader("error", "DB_QUERY_FAILED")
                                .addHeader("cause", ar.cause().getMessage()));
                        super.errorOutboundCount++;
                    }
                });
            }
        }
    }

    @Override
    public void healthCheck() {
        SharedData sharedData = vertx.sharedData();
        healthMap = sharedData.getLocalMap(String.join(".",config().getString("graph_id"),config().getString("type"),config().getString("id")));
        healthMap.put("errorOutboundCount", 0L);
        healthMap.put("resultOutboundCount", 0L);
        this.ports
                .put("trigger", this.triggerInboundCount)
                .put("input", this.inputInboundCount)
                .put("error", healthMap.get("errorOutboundCount"))
                .put("result", healthMap.get("resultOutboundCount"));
        this.health
                .put("type", addressBook.getType())
                .put("ports", this.ports);
        this.healthCheckHandler = ShareableHealthCheckHandler.create(vertx);
        this.healthCheckHandler.register(
                "status/" + config().getString("graph_id") + "/" + config().getString("id"),
                1000,
                promise -> {
                    promise.complete(Status.OK(this.health
                            .put("ports", this.ports
                                    .put("trigger", this.triggerInboundCount)
                                    .put("input", this.inputInboundCount)
                                    .put("error", healthMap.get("errorOutboundCount"))
                                    .put("result", healthMap.get("resultOutboundCount")))));
                });
    }
}
