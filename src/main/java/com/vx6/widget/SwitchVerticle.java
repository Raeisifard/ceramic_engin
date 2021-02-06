package com.vx6.widget;

import com.vx6.master.MasterVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.stream.Collectors;

public class SwitchVerticle extends MasterVerticle {
    private String kind;
    private Boolean state = false;
    private long timerID;
    private JsonObject body;
    private MultiMap headers = MultiMap.caseInsensitiveMultiMap();
    private int period = 0, delay = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        addressBook.setResultPortName("Output");
        addressBook.setResult();
        JsonObject data = config().getJsonObject("data");
        this.kind = data.getJsonObject("config").getString("kind");//trigger, relay, pushButton
        processSetting(data.getJsonObject("setting"));
        initPromise.complete();
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        if (kind.equalsIgnoreCase("trigger")) {
            subscribeTrigger();
        } else if (kind.equalsIgnoreCase("push")) {
            subscribePushButton();
        }
    }

    private void subscribeTrigger() {
        if (state) {
            if (period < 1 && delay < 1) {//without delay just once
                eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
            } else if (period >= 1 && delay < 1) {//periodic without delay
                eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
                timerID = vertx.setPeriodic(period, id -> {
                    eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
                });
            } else if (period < 1) {//just once with delay
                timerID = vertx.setTimer(delay, id -> {
                    eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
                });
            } else {//periodic with delay
                timerID = vertx.setTimer(delay, idt -> {
                    eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
                    timerID = vertx.setPeriodic(period, idp -> {
                        eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
                    });
                });
            }
        }
    }

    private void subscribePushButton() {
        //Initial state of every push button in "off", means no signal!
    }

    @Override
    public <T> void process(Message<T> tMessage) {
        if (this.state)
            eb.publish(addressBook.getResult(), tMessage.body(), new DeliveryOptions().setHeaders(tMessage.headers()));
    }

    @Override
    public <T> void set(Message<T> tMessage) {
        this.setting = new JsonObject(tMessage.body().toString());
        processSetting(this.setting);
        if (this.kind.equalsIgnoreCase("trigger")) {
            vertx.cancelTimer(timerID);
            subscribeTrigger();
            if (!(this.body == null || this.state)) {
                eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
            }
        } else if (this.kind.equalsIgnoreCase("push")) {
            eb.publish(addressBook.getResult(), this.body, new DeliveryOptions().setHeaders(headers));
        } else if (this.kind.equalsIgnoreCase("relay")) {

        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        vertx.cancelTimer(timerID);
        stopPromise.complete();
    }

    private void  processSetting(JsonObject setting) {
        if (kind.equalsIgnoreCase("trigger")) {
            this.state = setting.getJsonObject("_").getBoolean("state");
            this.period = setting.getJsonObject("_").getInteger("period", 0);
            this.delay = setting.getJsonObject("_").getInteger("delay", 0);
            setting.remove("_");
            if (setting.size() > 2 | !(setting.containsKey("on") || setting.containsKey("body"))) {
                if (this.state) {
                    setBody(setting);
                } else {
                    this.body = null;
                }
                return;
            }
            if (setting.size() == 2) {
                if (setting.containsKey("on") && setting.containsKey("off")) {
                    setIfBodyHeader(this.state ? setting.getJsonObject("on") : setting.getJsonObject("off"));
                    return;
                } else if (setting.containsKey("body") && setting.containsKey("header")) {
                    if (this.state) {
                        setBodyHeader(setting);
                    } else {
                        this.body = null;
                    }
                    return;
                }
            }
            if (setting.size() == 1) {
                if (setting.containsKey("on")) {
                    if (this.state) {
                        setIfBodyHeader(setting);
                    } else {
                        this.body = null;
                    }
                    return;
                } else if (setting.containsKey("body")) {
                    if (this.state) {
                        setBody(setting.getJsonObject("body"));
                    } else {
                        this.body = null;
                    }
                    return;
                }
            }
            setBody(setting);
        } else if (kind.equalsIgnoreCase("push")) {
            setIfBodyHeader(setting);
        } else if (kind.equalsIgnoreCase("relay")) {
            this.state = setting.getJsonObject("_").getBoolean("state");
        }
    }

    private void setBodyHeader(JsonObject setting) {
        this.body = setting.getJsonObject("body");
        this.headers = MultiMap.caseInsensitiveMultiMap().addAll(setting.getJsonObject("header").getMap().entrySet().stream()
          .collect(Collectors.toMap(Map.Entry::getKey, e -> (String)e.getValue())));
    }

    private void setIfBodyHeader(JsonObject setting) {
        if (setting.size() == 2 && setting.containsKey("body") && setting.containsKey("header")) {
            setBodyHeader(setting);
        } else if (setting.size() == 1 && setting.containsKey("body")) {
            setBody(setting.getJsonObject("body"));
        } else {
            setBody(setting);
        }
    }

    private void setBody(JsonObject setting) {
        this.body = setting.copy();
        this.headers = MultiMap.caseInsensitiveMultiMap();
    }
}
