package com.vx6.widget;

import com.ceramic.shared.ShareableHealthCheckHandler;
import com.ibm.mq.MQException;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.vx6.master.MasterVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.Status;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.Hashtable;

public class IbmMqVerticle extends MasterVerticle {
    private String qm, qName;
    private MQQueueManager qMgr = null;
    private MQQueue queue = null;
    private long count = 0;
    private int depth = -1;
    private final Hashtable<String, Object> mqProperties = new Hashtable<String, Object>();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        JsonObject config = config().getJsonObject("data").getJsonObject("config");
        /*if (config.getString("ip") == null) {
            initPromise.fail("MQ ip must be something not null");
        } else */
        if (config.getString("ip") != null && config.getString("ip").trim().charAt(0) == '#') {
            String dataSourceName = config.getString("ip").trim().substring(1);
            if (config().containsKey("dataSource")) {
                if (config().getJsonObject("dataSource").containsKey(dataSourceName)) {
                    JsonObject dataSource = config().getJsonObject("dataSource").getJsonObject(dataSourceName).copy();
                    config.put("ip", dataSource.getString("ip"));
                    if (config.getInteger("port") == null || config.getInteger("port") <= 0)
                        config.put("port", dataSource.getInteger("port"));
                    if (StringUtils.isBlank(config.getString("qm")))
                        config.put("qm", dataSource.getString("qm"));
                    if (StringUtils.isBlank(config.getString("qName")))
                        config.put("qName", dataSource.getString("qName"));
                    if (StringUtils.isBlank(config.getString("channelName")))
                        config.put("channelName", dataSource.getString("channelName"));
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
        if (config.getString("ip") == null)
            config.put("ip", "172.0.0.1");
        if (config.getInteger("port") == null)
            config.put("port", 1414);
        if (config.getString("channelName") == null)
            config.put("channelName", "SYSTEM.DEF.SVRCONN");
        if (config.getString("codePage") == null)
            config.put("codePage", "1098");

        qm = config.getString("qm");
        qName = config.getString("qName");
        if (!inputConnected) {
            mqProperties.put(MQConstants.HOST_NAME_PROPERTY, config.getString("ip"));
            mqProperties.put(MQConstants.PORT_PROPERTY, config.getInteger("port"));
            mqProperties.put(MQConstants.USER_ID_PROPERTY, config.getString("user"));
            mqProperties.put(MQConstants.PASSWORD_PROPERTY, config.getString("pass"));
            mqProperties.put(MQConstants.CHANNEL_PROPERTY, config.getString("channelName"));
            mqProperties.put(MQConstants.CCSID_PROPERTY, config.getString("codePage"));
            connectToMq();
        }
        DeploymentOptions options = new DeploymentOptions().setWorker(false);
        if (config.containsKey("instance")) {
            options.setInstances(config.getInteger("instance"));
        }
        config.put("ibmmqverticleid", deploymentID());
        options.setConfig(config);
        vertx.deployVerticle(inputConnected ? "com.vx6.widget.ibm.websphere.IbmMqWriteVerticle" :
                "com.vx6.widget.ibm.websphere.IbmMqReadVerticle", options, result -> {
            if (result.succeeded()) {
                initPromise.complete();
            } else {
                initPromise.fail(result.result());
                result.cause().printStackTrace();
            }
        });
    }

    @Override
    public <T> void process(Message<T> tMessage) {//Writing mode.
        eb.request(deploymentID(), tMessage.body(), ar -> {
            if (ar.succeeded()) {
                if (outputConnected)
                    this.resultOutboundCount++;
                super.process(tMessage);
            } else {
                eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions(tMessage)
                        .addHeader("error", "MQ_WRITE_FAILED")
                        .addHeader("cause", ar.cause().getMessage()));
                this.errorOutboundCount++;
            }
        });
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        if (!inputConnected) {
            JsonObject jo = (JsonObject) tMessage.body();
            int number = jo.getInteger("number", 1);
            try {
                if (!(queue.isOpen() || qMgr.isConnected()))
                    try {
                        connectToMq();
                    } catch (Exception e) {
                        sendException(e);
                    }
                depth = queue.getCurrentDepth();
                eb.publish(addressBook.getError(), new JsonObject().put("depth", depth).put("number", number)
                        .put("count", count).put("queueManagerName", qm).put("queueName", qName), addressBook.getDeliveryOptions());
                this.errorOutboundCount++;
                int counter = Math.min(depth, number);
                for (int i = 0; i < counter; i++) {
                    eb.request(deploymentID(), "read", ar -> {
                        count++;
                        DeliveryOptions dOpt = addressBook.getDeliveryOptions();
                        dOpt.addHeader("count", count + "")
                                .addHeader("queueManagerName", qm)
                                .addHeader("queueName", qName)
                                .addHeader("timeStamp", String.valueOf(new Date()));
                        if (ar.succeeded()) {
                            eb.publish(addressBook.getResult(), ar.result().body(), dOpt);
                            this.resultOutboundCount++;
                        } else {//ar.failed()
                            //eb.publish(addressBook.getError(), ar.toString(), dOpt.setHeaders(ar.result().headers()));
                        }
                    });
                }
            } catch (MQException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            if (queue != null) {
                queue.close();
            }
            if (qMgr != null)
                qMgr.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.stop();
    }

    @Override
    public void healthCheck() {
        this.healthCheckHandler = ShareableHealthCheckHandler.create(vertx);
        JsonObject jo = new JsonObject()
                .put("type", addressBook.getType())
                .put("state", inputConnected ? "write" : "read")
                .put("qMgr", qm)
                .put("qName", qName);
        JsonObject ports = new JsonObject();
        this.healthCheckHandler.register(
                "status/" + config().getString("graph_id") + "/" + config().getString("id"),
                1000,
                promise -> {
                    if (triggerConnected) {
                        ports.put("trigger", this.triggerInboundCount);
                        jo.put("depth", depth);
                    }
                    if (inputConnected)
                        ports.put("input", this.inputInboundCount);
                    ports.put("error", this.errorOutboundCount)
                            .put("result", this.resultOutboundCount);
                    promise.complete(Status.OK(jo.put("ports", ports)));
                });
    }

    private void connectToMq() throws Exception {
        qMgr = new MQQueueManager(qm, mqProperties);
        queue = qMgr.accessQueue(qName, CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_INQUIRE | CMQC.MQOO_NO_READ_AHEAD);
    }
}
