package com.vx6.widget;

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

import java.util.Date;
import java.util.Hashtable;

public class IbmMqVerticle extends MasterVerticle {
  String qm, qName;
  MQQueueManager qMgr = null;
  MQQueue queue = null;
  long count = 0;

  @Override
  public void initialize(Promise<Void> initPromise) throws Exception {
    DeploymentOptions options = new DeploymentOptions().setWorker(false);
    if (config().getJsonObject("data").getJsonObject("config").containsKey("instance")) {
      options.setInstances(config().getJsonObject("data").getJsonObject("config").getInteger("instance"));
    }
    config().put("ibmmqverticleid", deploymentID());
    options.setConfig(config());
    if (!inputConnected) {
      final Hashtable<String, Object> mqProperties = new Hashtable<String, Object>();
      JsonObject config = config().getJsonObject("data").getJsonObject("config");
      qm = config.getString("qm");
      qName = config.getString("qName");
      mqProperties.put(MQConstants.HOST_NAME_PROPERTY, config.getString("ip"));
      mqProperties.put(MQConstants.PORT_PROPERTY, config.getInteger("port"));
      mqProperties.put(MQConstants.USER_ID_PROPERTY, config.getString("user"));
      mqProperties.put(MQConstants.PASSWORD_PROPERTY, config.getString("pass"));
      mqProperties.put(MQConstants.CHANNEL_PROPERTY, config.getString("channelName"));
      mqProperties.put(MQConstants.CCSID_PROPERTY, config.getString("codePage"));
      qMgr = new MQQueueManager(qm, mqProperties);
      queue = qMgr.accessQueue(qName, CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_INQUIRE | CMQC.MQOO_NO_READ_AHEAD);
    }
    vertx.deployVerticle(inputConnected ? "com.vx6.widget.ibm.websphere.IbmMqWriteVerticle" : "com.vx6.widget.ibm.websphere.IbmMqReadVerticle", options, result -> {
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
          super.process(tMessage);
      } else {
        eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions(tMessage)
          .addHeader("error", "MQ_WRITE_FAILED")
          .addHeader("cause", ar.cause().getMessage()));
      }
    });
  }

  @Override
  public <T> void noCmd(Message<T> tMessage, String cmd) {
    if (!inputConnected) {
      JsonObject jo = (JsonObject) tMessage.body();
      int number = jo.getInteger("number", 1);
      try {
        int depth = queue.getCurrentDepth();
        eb.publish(addressBook.getError(), new JsonObject().put("depth", depth).put("number", number)
          .put("count", count).put("queueManagerName", qm).put("queueName", qName));
        int counter = Math.min(depth, number);
        for (int i = 0; i < counter; i++) {
          eb.request(deploymentID(), "read", ar -> {
            if (ar.succeeded()) {
              count++;
              DeliveryOptions dOpt = new DeliveryOptions();
              dOpt.addHeader("id", count + "")
                .addHeader("queueManagerName", qm)
                .addHeader("queueName", qName)
                .addHeader("timeStamp", String.valueOf(new Date()));
              eb.publish(addressBook.getResult(), ar.result().body());
            } else {//ar.failed()
              eb.publish(addressBook.getError(), ar.result().body(), new DeliveryOptions().setHeaders(ar.result().headers()));
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
}
