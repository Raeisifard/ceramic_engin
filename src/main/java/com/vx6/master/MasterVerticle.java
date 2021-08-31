package com.vx6.master;

//import com.sun.org.apache.bcel.internal.classfile.Unknown;

import com.ceramic.shared.ShareableHealthCheckHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.LinkedList;
import java.util.Queue;

enum Cmd {
    SET,
    UPDATE,
    NEXT,
    FLUSH,
    PAUSE,
    RESUME,
    CLOSE,
    OPEN,
    STOP,
    START,
    READY,
    BUFFER_DRAINED,
    BUFFER_FILLED
}

public class MasterVerticle extends AbstractVerticle {
    public EventBus eb;
    public JsonObject setting = new JsonObject();
    public boolean inputConnected = false, outputConnected = false, errorConnected = false, triggerConnected = false;
    public int bufferSize = 0;
    //public Queue<Message> q = new LinkedList<>();//Input queue
    public int lt, ht;//low threshold & high threshold
    public boolean holdOn = true, pushedBack = false, autoNext = true, ready = false;
    public AddressBook addressBook;
    public Buffer buffer = null;
    protected long triggerInboundCount = 0, inputInboundCount = 0, errorOutboundCount = 0, resultOutboundCount = 0;
    protected JsonObject ports = new JsonObject(), health = new JsonObject();
    protected HealthCheckHandler healthCheckHandler;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        start();
        initialize(startPromise);
        //startFuture.complete();
    }

    public void initialize(Promise<Void> initPromise) throws Exception {
        initPromise.complete();
    }

    @Override
    public void start() throws Exception {
        this.eb = vertx.eventBus();
        setting = config().getJsonObject("data").getJsonObject("setting");
        addressBook = new AddressBook(config());
        if (config().containsKey("Result")) {
            outputConnected = !config().getJsonArray("Result").isEmpty();
        }
        if (config().containsKey("Input")) {
            inputConnected = !config().getJsonArray("Input").isEmpty();
        }
        if (config().containsKey("Error")) {
            errorConnected = !config().getJsonArray("Error").isEmpty();
        }
        if (config().containsKey("Trigger")) {
            triggerConnected = !config().getJsonArray("Trigger").isEmpty();
        }
        if (inputConnected) {
            bufferSize = config().getJsonObject("data").getJsonObject("config").getInteger("BUFFER_SIZE", 0);
            if (bufferSize > 0)
                buffer = new Buffer(eb, addressBook.getTrigger(), bufferSize);
        }
        healthCheck();
        initConsumers();
    }

    public void healthCheck() {
        this.ports
                .put("trigger", this.triggerInboundCount)
                .put("input", this.inputInboundCount)
                .put("error", this.errorOutboundCount)
                .put("result", this.resultOutboundCount);
        if (addressBook.getType().equalsIgnoreCase("process") && config().getJsonObject("data").containsKey("outNumber")) {
            //Include outs in ports stats...
            var outNumber = config().getJsonObject("data").getInteger("outNumber");
            for (int i = 0; i < outNumber; i++) {
                this.ports.put("out" + i, 0);
            }
        }
        this.health.put("type", addressBook.getType());
        if (addressBook.getType().equalsIgnoreCase("process")) {
            this.health.put("class", this.getClass().getName());
        }
        this.health.put("ports", this.ports);
        this.healthCheckHandler = ShareableHealthCheckHandler.create(vertx);
        this.healthCheckHandler.register(
                "status/" + config().getString("graph_id") + "/" + config().getString("id"),
                1000,
                promise -> {
                    promise.complete(Status.OK(getHealth()));
                });
    }

    protected JsonObject getHealth() {
        return this.health
                .put("ports", this.ports
                        .put("trigger", this.triggerInboundCount)
                        .put("input", this.inputInboundCount)
                        .put("error", this.errorOutboundCount)
                        .put("result", this.resultOutboundCount));
    }

    public void initConsumers() {
        this.eb.consumer(addressBook.getGraph_id(), this::trigger);
        this.eb.consumer(addressBook.getTrigger(), this::trigger);
        //addressBook.getTriggerIns().forEach(adrs -> eb.consumer(adrs, this::trigger));
        addressBook.getTriggerIns().forEach(adrs -> eb.consumer(adrs, message -> {
            this.triggerInboundCount++;
            this.trigger(message);
        }));
        //addressBook.getInputIns().forEach(adrs -> eb.consumer(adrs, this::input));
        addressBook.getInputIns().forEach(adrs -> eb.consumer(adrs, message -> {
            this.inputInboundCount++;
            this.input(message);
        }));
    }

    public <T> void input(Message<T> tMessage) {
        try {
            if (buffer != null) {
                buffer.putMessage(tMessage);
                if (autoNext && !holdOn)
                    eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
            } else {
                process(tMessage);
                this.resultOutboundCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> void process(Message<T> tMessage) {
        //TODO Overload this method to set up your process.
        eb.publish(addressBook.getResult(), process(tMessage.body().toString()), addressBook.getDeliveryOptions(tMessage));//.addHeader("buffer_size", buffer.getBuffSize() + ""));
    }

    public String process(String msg) {
        //TODO Overload this method to set up your process.
        return msg;
    }

    public <T> void trigger(Message<T> tMessage) {
        String cmd = "";
        if (tMessage.headers().contains("cmd")) {
            cmd = tMessage.headers().get("cmd");
            Cmd command;
            try {
                command = Cmd.valueOf(cmd.toUpperCase());
                switch (command) {
                    case NEXT:
                        if (!holdOn)
                            next(tMessage);
                        break;
                    case SET:
                        set(tMessage);
                        break;
                    case UPDATE:
                        update(tMessage);
                        break;
                    case PAUSE:
                        pause(tMessage);
                        break;
                    case RESUME:
                        resume(tMessage);
                        break;
                    case BUFFER_FILLED:
                        addressBook.getPushBackPorts().forEach(adrs -> eb.publish(adrs, "Pause", new DeliveryOptions().addHeader("cmd", "pause")));
                        break;
                    case BUFFER_DRAINED:
                        addressBook.getPushBackPorts().forEach(adrs -> eb.publish(adrs, "Resume", new DeliveryOptions().addHeader("cmd", "resume")));
                        break;
                    case FLUSH:
                        flush(tMessage);
                        break;
                    case START:
                        start(tMessage);
                        break;
                    case READY:
                        this.ready = true;
                        ready(tMessage);
                        break;
                    case OPEN:
                        open(tMessage);
                        break;
                    case CLOSE:
                        close(tMessage);
                        break;
                    case STOP:
                        stop(tMessage);
                        break;
                    default:
                        defaultCmd(tMessage, cmd);
                        break;
                }
            } catch (Exception e) {
                unknownCmd(tMessage, cmd);
            }
        } else {
            noCmd(tMessage, cmd);
        }
    }

    public <T> void open(Message<T> tMessage) {
        open();
    }

    public void open() {

    }

    public <T> void close(Message<T> tMessage) {
        close();
    }

    public void close() {
    }

    public <T> void start(Message<T> tMessage) {
        this.ready = true;
        ready(tMessage);
    }

    public <T> void unknownCmd(Message<T> tMessage, String cmd) {
        eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions(tMessage).addHeader("error", "UNKNOWN_CMD"));
        this.errorOutboundCount++;
    }

    public <T> void defaultCmd(Message<T> tMessage, String cmd) {
        eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions(tMessage).addHeader("error", "DEFAULT_CMD"));
        this.errorOutboundCount++;
    }

    public <T> void noCmd(Message<T> tMessage, String cmd) {
        eb.publish(addressBook.getError(), tMessage.body(), addressBook.getDeliveryOptions(tMessage).addHeader("error", "NO_CMD"));
        this.errorOutboundCount++;
    }

    public <T> void ready(Message<T> tMessage) {
        holdOn = false;
        if (autoNext) {
            eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
        }
    }

    public <T> void set(Message<T> tMessage) {
        setting = new JsonObject(tMessage.body().toString());
    }

    public <T> void update(Message<T> tMessage) {
        setting.mergeIn(new JsonObject(tMessage.body().toString()), true);
    }

    public <T> void pause(Message<T> tMessage) {
        holdOn = true;
        if (inputConnected && buffer == null) {
            addressBook.getPushBackPorts().forEach(adrs -> eb.publish(adrs, "Pause", new DeliveryOptions().addHeader("cmd", "pause")));
            pushedBack = true;
        }
    }

    public <T> void resume(Message<T> tMessage) {
        holdOn = false;
        if (inputConnected && buffer == null) {
            addressBook.getPushBackPorts().forEach(adrs -> eb.publish(adrs, "Resume", new DeliveryOptions().addHeader("cmd", "resume")));
            pushedBack = false;
        } else if (autoNext)
            eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
    }

    public <T> void next(Message<T> tMessage) {
        if (inputConnected && buffer != null && !holdOn) {
            Message msg = buffer.getMessage();
            if (msg != null) {
                process(tMessage);
                this.resultOutboundCount++;
                if (autoNext) {
                    eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
                }
            }
        }
    }

    public void flush() throws Exception {
    }

    public <T> void flush(Message<T> tMessage) {
        try {
            flush();
        } catch (Exception e) {
            sendException(e);
        }
    }

    public void publishOut(int i, Object body) {
        publishOut(i, body, null);
    }

    public void publishOut(int i, Object body, DeliveryOptions dO) {
        if (this.ports.containsKey("out" + i)) {
            this.ports.put("out" + i, this.ports.getInteger("out" + i) + 1);
        }
        if (dO != null)
            eb.publish(addressBook.getOut("out" + i), body, dO);
        else
            eb.publish(addressBook.getOut("out" + i), body);
    }

    public <T> void stop(Message<T> tMessage) {
        this.ready = false;
        try {
            flush(tMessage);
            stop();
        } catch (Exception e) {
            sendException(e);
        }
    }

    @Override
    public void stop() throws Exception {
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        stop();
        this.healthCheckHandler.unregister("status/" + config().getString("graph_id") + "/" + config().getString("id"));
        stopPromise.complete();
    }

    protected void sendException(String s, Exception e) {
        eb.publish(addressBook.getError(), s, addressBook.getDeliveryOptions().addHeader("type", "error").addHeader("stack-trace", ExceptionUtils.getStackTrace(e)));
        this.errorOutboundCount++;
    }

    protected void sendException(Exception e) {
        eb.publish(addressBook.getError(), ExceptionUtils.getStackTrace(e), addressBook.getDeliveryOptions().addHeader("type", "error"));
        this.errorOutboundCount++;
    }
}