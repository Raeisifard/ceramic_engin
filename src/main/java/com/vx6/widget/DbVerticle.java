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
import org.apache.commons.lang3.StringUtils;

public class DbVerticle extends MasterVerticle {
    long count = 0;
    private LocalMap<String, Long> healthMap;
    private Integer instance = 1;
    //private Integer rate = null;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        JsonObject data = config().getJsonObject("data");
        String dbType = data.getString("dbType", "SqlServer");
        JsonObject config = data.getJsonObject("config");

        /*if (config.getString("ip") == null) {
            initPromise.fail("Database ip must be something not null");
        } else*/
        if (config.getString("ip") != null && config.getString("ip").trim().charAt(0) == '#') {
            String dataSourceName = config.getString("ip").trim().substring(1);
            if (config().containsKey("dataSource")) {
                if (config().getJsonObject("dataSource").containsKey(dataSourceName)) {
                    JsonObject dataSource = config().getJsonObject("dataSource").getJsonObject(dataSourceName).copy();
                    config.put("ip", dataSource.getString("ip"));
                    if (config.getInteger("port") == null || config.getInteger("port") <= 0)
                        config.put("port", dataSource.getInteger("port"));
                    if (StringUtils.isBlank(config.getString("dbName")))
                        config.put("dbName", dataSource.getString("dbName"));
                    if (StringUtils.isBlank(config.getString("user")))
                        config.put("user", dataSource.getString("user"));
                    if (StringUtils.isBlank(config.getString("pass")))
                        config.put("pass", dataSource.getString("pass"));
                    config = dataSource.copy().mergeIn(config).put("ip", dataSource.getString("ip"));
                } else {
                    initPromise.fail("Could not find \"" + dataSourceName + "\" in Data Sources.");
                }
            } else {
                initPromise.fail("Data Sources are not defined but you named one: " + dataSourceName);
            }
        }
        if (config.getString("ip") == null) {
            config.put("ip", "172.0.0.1");
        }

        instance = config.getInteger("instance", 1);
        config.put("instance", instance);
        config.put("query", data.getJsonObject("setting").getString("query"));
        config.put("dbverticleid", deploymentID());
        DeploymentOptions options = new DeploymentOptions().setWorker(false);
        options.setConfig(config().put("config", config));
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
        /*if (tMessage.body().toString().equalsIgnoreCase("ready"))
            return;*/
        if (this.instance == 1)
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
            /*if (jo.containsKey("rate"))
                this.rate = jo.getInteger("rate");*/
        }
    }

    @Override
    public void healthCheck() {
        SharedData sharedData = vertx.sharedData();
        healthMap = sharedData.getLocalMap(String.join(".", config().getString("graph_id"), config().getString("type"), config().getString("id")));
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
