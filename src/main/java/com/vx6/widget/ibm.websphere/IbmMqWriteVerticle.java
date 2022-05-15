package com.vx6.widget.ibm.websphere;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.util.Hashtable;

public class IbmMqWriteVerticle  extends AbstractVerticle {

  private EventBus eb;
  private final Hashtable<String, Object> mqProperties = new Hashtable<String, Object>();
  private String qManagerName;
  private String queueName;
  private MQQueueManager qMgr;
  private MQQueue queue;

  @Override
  public void start(Promise<Void> initPromise) throws Exception {
    this.eb = vertx.eventBus();
    JsonObject config = config();
    String hostName = config.getString("ip");
    int port = config.getInteger("port");
    String userName = config.getString("user");
    String password = config.getString("pass");
    this.qManagerName = config.getString("qm");
    this.queueName = config.getString("qName");
    String channelName = config.getString("channelName");
    String codePage = config.getString("codePage", "1098");

    //define message Consumers
    eb.consumer(config.getString("ibmmqverticleid"), this::process);
    mqProperties.put(MQConstants.HOST_NAME_PROPERTY, hostName);
    mqProperties.put(MQConstants.PORT_PROPERTY, port);
    mqProperties.put(MQConstants.USER_ID_PROPERTY, userName);
    mqProperties.put(MQConstants.PASSWORD_PROPERTY, password);
    mqProperties.put(MQConstants.CHANNEL_PROPERTY, channelName);
    mqProperties.put(MQConstants.CCSID_PROPERTY, codePage);
    connectMQ(initPromise);
  }

  private <T> void process(Message<T> tMessage) {
    MQMessage putMessage = new MQMessage();
    MQPutMessageOptions mQPutMessageOptions = new MQPutMessageOptions();
    putMessage.format = "MQSTR";
    putMessage.characterSet = 1208;
    try {
      putMessage.writeString(tMessage.body().toString());
      queue.put(putMessage, mQPutMessageOptions);
      tMessage.reply("ok");
    } catch (MQException | IOException e) {
      tMessage.fail(8, e.getMessage());
    }
  }

  private void connectMQ(Promise<Void> initPromise) {
    try {
      qMgr = new MQQueueManager(qManagerName, mqProperties);
      int openOptions = CMQC.MQOO_OUTPUT | CMQC.MQOO_INQUIRE;
      queue = qMgr.accessQueue(queueName, openOptions);
      MQException.logExclude(MQConstants.MQRC_NO_MSG_AVAILABLE);
      initPromise.complete();
    } catch (Exception e) {
      e.printStackTrace();
      initPromise.fail(e);
    }
  }

  @Override
  public void stop(Promise<Void> initPromise) throws Exception {
    shutdown();
    initPromise.complete();
  }

  private void shutdown() {
    try {
      if (queue != null) {
        queue.close();
      }
      if (qMgr != null)
        qMgr.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      queue = null;
      qMgr = null;
    }
  }
}
