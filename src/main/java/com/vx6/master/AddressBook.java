package com.vx6.master;

import io.netty.util.internal.StringUtil;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class AddressBook {
    private String graph_name = null, graph_id = null, type = null, id = null, class_name = null;
    private String triggerPortName = "Trigger", inputPortName = "Input", errorPortName = "Error", resultPortName = "Result";
    private String error = null, result = null; //Address for pub.
    private String trigger = null; //Address for pub/sub.
    private static String client = null, master = null; //Address for pub/sub.
    private final List<String> triggerIns = new ArrayList<>(), inputIns = new ArrayList<>(); //Address for sub.
    private final List<String> pushBackAddresses = new ArrayList<>(); //Address for pub.
    private JsonObject port;
    protected long triggerInboundCount = 0, inputInboundCount = 0, errorOutboundCount = 0, resultOutboundCount = 0;
    public AddressBook(String graph_id) {
        this.graph_id = graph_id;
    }

    public AddressBook(JsonObject config) {
        this(config.getString("graph_id"));
        if (config.containsKey("port")) {
            this.port = config.getJsonObject("port");
            setTriggerPortName(this.port.getString("triggerPortName", getTriggerPortName()));
            setInputPortName(this.port.getString("inputPortName", getInputPortName()));
            setErrorPortName(this.port.getString("errorPortName", getErrorPortName()));
            setResultPortName(this.port.getString("resultPortName", getResultPortName()));
        }
        this.graph_name = config.getString("graph_name");
        this.type = config.getString("type");
        this.id = config.getString("id");
        if (this.type != null && this.type.equals("process"))
            this.class_name = config.getJsonObject("data").getString("fName");
        error = String.join(".", graph_id, type, id, errorPortName).toLowerCase();
        result = String.join(".", graph_id, type, id, resultPortName).toLowerCase();
        trigger = String.join(".", graph_id, type, id, triggerPortName).toLowerCase();
        client = graph_id;
        master = graph_id;
        if (config.containsKey(triggerPortName)) {
            config.getJsonArray(triggerPortName).forEach(adrs -> {
                triggerIns.add(String.join(".", graph_id, adrs.toString()).toLowerCase());
            });
        }
        if (config.containsKey(inputPortName)) {
            config.getJsonArray(inputPortName).forEach(adrs -> {
                inputIns.add(String.join(".", graph_id, adrs.toString()).toLowerCase());
            });
        }
        inputIns.forEach(port -> this.pushBackAddresses.add(String.join(".", port.substring(0, port.lastIndexOf(".")), "trigger").toLowerCase()));
    }

    public final DeliveryOptions getDeliveryOptions() {
        if (class_name != null)
            return new DeliveryOptions().addHeader("graph_name", graph_name).addHeader("graph_id", graph_id).addHeader("type", type).addHeader("id", id).addHeader("class", class_name);
        else
            return new DeliveryOptions().addHeader("graph_name", graph_name).addHeader("graph_id", graph_id).addHeader("type", type).addHeader("id", id);
    }

    public final DeliveryOptions getDeliveryOptions(Message tMessage) {
        if (StringUtil.isNullOrEmpty(type) || StringUtil.isNullOrEmpty(id) || StringUtil.isNullOrEmpty(graph_id)) {
            return new DeliveryOptions().setHeaders(tMessage.headers());
        } else {
            DeliveryOptions deliveryOption = new DeliveryOptions().addHeader("graph_id", graph_id).addHeader("type", type).addHeader("id", id);
            tMessage.headers().entries().forEach(item -> {
                String itemName = item.getKey();
                if (!(itemName.equalsIgnoreCase("graph_id") || itemName.equalsIgnoreCase("type") || itemName.equalsIgnoreCase("id"))) {
                    deliveryOption.addHeader(item.getKey(), item.getValue());
                }
            });
            return deliveryOption;
        }
    }

    public final DeliveryOptions getDeliveryOptions(DeliveryOptions deliveryOptions) {
        return deliveryOptions.addHeader("type", type).addHeader("id", id).addHeader("graph_id", graph_id);
    }

    public final DeliveryOptions getDeliveryOptions(DeliveryOptions deliveryOptions, Message tMessage) {
        return deliveryOptions.setHeaders(tMessage.headers()).addHeader("type", type).addHeader("id", id).addHeader("graph_id", graph_id);
    }

    public String getGraph_id() {
        return graph_id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setTriggerPortName(String triggerPortName) {
        this.triggerPortName = triggerPortName;
    }

    public void setInputPortName(String inputPortName) {
        this.inputPortName = inputPortName;
    }

    public void setErrorPortName(String errorPortName) {
        this.errorPortName = errorPortName;
    }

    public void setResultPortName(String resultPortName) {
        this.resultPortName = resultPortName;
    }

    public String getTriggerPortName() {
        return triggerPortName;
    }

    public String getInputPortName() {
        return inputPortName;
    }

    public String getErrorPortName() {
        return errorPortName;
    }

    public String getResultPortName() {
        return resultPortName;
    }

    public String getError() {
        return error;
    }

    public void setResult() {
        result = String.join(".", graph_id, type, id, resultPortName).toLowerCase();
    }

    public String getResult() {
        return result;
    }

    public String getTrigger() {
        return trigger;
    }

    public String getClient() {
        return client;
    }

    public String getMaster() {
        return master;
    }

    public List<String> getTriggerIns() {
        return triggerIns;
    }

    public List<String> getInputIns() {
        return inputIns;
    }

    public List<String> getpushBackAddresses() {
        return pushBackAddresses;
    }

    public String getOut(String outName) {
        return String.join(".", graph_id, type, id, outName).toLowerCase();
    }

    public long getTriggerInboundCount() {
        return triggerInboundCount;
    }

    public void setTriggerInboundCount(long triggerInboundCount) {
        this.triggerInboundCount = triggerInboundCount;
    }

    public long getInputInboundCount() {
        return inputInboundCount;
    }

    public void setInputInboundCount(long inputInboundCount) {
        this.inputInboundCount = inputInboundCount;
    }

    public long getErrorOutboundCount() {
        return errorOutboundCount;
    }

    public void setErrorOutboundCount(long errorOutboundCount) {
        this.errorOutboundCount = errorOutboundCount;
    }

    public long getResultOutboundCount() {
        return resultOutboundCount;
    }

    public void setResultOutboundCount(long resultOutboundCount) {
        this.resultOutboundCount = resultOutboundCount;
    }
}
