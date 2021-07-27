package com.vx6.widget.ibm.websphere;

import com.ibm.mq.*;
import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.vx6.utils.Ebcdic;
import com.vx6.utils.Log;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;

//This class is for mode == "reading"
public class IbmMqReadVerticle extends AbstractVerticle {
    private EventBus eb;
    private boolean readAhead = false;
    private final Hashtable<String, Object> mqProperties = new Hashtable<String, Object>();
    private String qManagerName;
    private String queueName;
    private MQQueueManager qMgr;
    private MQQueue queue;

    @Override
    public void start(Promise<Void> initPromise) {
        this.eb = vertx.eventBus();
        JsonObject config = config().getJsonObject("data").getJsonObject("config");
        String hostName = config.getString("ip");
        int port = config.getInteger("port");
        String userName = config.getString("user");
        String password = config.getString("pass");
        this.qManagerName = config.getString("qm");
        this.queueName = config.getString("qName");
        String channelName = config.getString("channelName");
        this.readAhead = config.getBoolean("readAhead", false);
        String codePage = config.getString("codePage", "1098");

        //define message Consumers
        eb.consumer(config().getString("ibmmqverticleid"), this::process);
        mqProperties.put(MQConstants.HOST_NAME_PROPERTY, hostName);
        mqProperties.put(MQConstants.PORT_PROPERTY, port);
        mqProperties.put(MQConstants.USER_ID_PROPERTY, userName);
        mqProperties.put(MQConstants.PASSWORD_PROPERTY, password);
        mqProperties.put(MQConstants.CHANNEL_PROPERTY, channelName);
        mqProperties.put(MQConstants.CCSID_PROPERTY, codePage);
        connectMQ(initPromise);
    }

    private <T> void process(Message<T> tMessage) {
        MQMessage getMessage = new MQMessage();
        try {
            queue.get(getMessage, new MQGetMessageOptions());
            String format = getMessage.format.trim();
            String txt = "";
            if (format.equalsIgnoreCase("MQSTR") || format.isEmpty()) {
                if (getMessage.characterSet == 1097) {
                    byte[] b = null;
                    try {
                        b = new byte[getMessage.getDataLength()];
                    } catch (IOException e) {
                        e.printStackTrace();
                        tMessage.fail(10, "io exception");
                    }
                    try {
                        getMessage.readFully(b, 0, getMessage.getDataLength());
                    } catch (IOException e) {
                        e.printStackTrace();
                        tMessage.fail(10, "io exception");
                    }
                    txt = Ebcdic.ConvertToUnicode(b);
                } else {
                    try {
                        txt = getMessage.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                        tMessage.fail(10, "io exception");
                    }
                }
            } else if (format.equalsIgnoreCase("MQHRF2")) {
                byte[] b = new byte[0];
                try {
                    b = new byte[getMessage.getDataLength()];
                } catch (IOException e) {
                    e.printStackTrace();
                    tMessage.fail(10, "io exception");
                }
                try {
                    getMessage.readFully(b, 0, getMessage.getDataLength());
                } catch (IOException e) {
                    e.printStackTrace();
                    tMessage.fail(10, "io exception");
                }
                txt = DecodeUtf8(b).substring(160);
            } else {
                Log.log.addLog("Cannot Process MQ Format =" + format);
            }
            tMessage.reply(txt);
        } catch (MQException e) {
            if (e.getReason() == 2033) { // empty queue
                tMessage.fail(2033, "empty queue");
            } else {
                tMessage.fail(8, "mq exception");
                e.printStackTrace();
            }
        } catch (Exception e){
            tMessage.fail(0, "general exception");
        }
    }

    private void connectMQ(Promise<Void> initPromise) {
        try {
            qMgr = new MQQueueManager(qManagerName, mqProperties);
            int openOptions;
            if (readAhead)
                openOptions = CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_INQUIRE | CMQC.MQOO_READ_AHEAD;
            else
                openOptions = CMQC.MQOO_INPUT_SHARED | CMQC.MQOO_INQUIRE | CMQC.MQOO_NO_READ_AHEAD;
            queue = qMgr.accessQueue(queueName, openOptions);
            MQException.logExclude(MQConstants.MQRC_NO_MSG_AVAILABLE);
            initPromise.complete();
        } catch (Exception e) {
            e.printStackTrace();
            initPromise.fail(e);
        }
    }

    private static String DecodeUtf8(byte[] ByteArray) {
        Charset UTF_8 = StandardCharsets.UTF_8;
        return new String(ByteArray, 0, ByteArray.length, UTF_8);
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
