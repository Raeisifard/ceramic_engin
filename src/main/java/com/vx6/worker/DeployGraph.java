package com.vx6.worker;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxXmlUtils;
import com.vx6.master.AddressBook;
import com.vx6.tools.MultipartStringMessage;
import com.vx6.utils.GraphProcess;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.file.FileSystem;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DeployGraph extends AbstractVerticle {
    private EventBus eb;
    private static final String tempPath = "./temp/";
    private String subTempPath;
    private Timestamp timestamp;
    private List<Future> futures = new ArrayList<>();
    private boolean fixedTempPath = false;
    private boolean clearTempPath = false;
    private boolean useTimestampInTempPath = false;
    private String graph_name, graph_id;
    private AddressBook addressBook;
    private String graph_xml;
    private mxGraphModel objGraph;
    private MultipartStringMessage msm;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        msm = new MultipartStringMessage(vertx, config().getJsonObject("config").getJsonObject("multiChunksMessage"));
        this.eb = vertx.eventBus();
        futures.clear();
        JsonObject graphProfile = config().getJsonObject("graphProfile");
        JsonObject config = config().getJsonObject("config");
        this.fixedTempPath = config.getBoolean("fixedTempPath");
        this.clearTempPath = config.getBoolean("clearTempPath");
        this.useTimestampInTempPath = config.getBoolean("useTimestampInTempPath");
        graph_name = graphProfile.getString("graph_name");
        graph_id = graphProfile.getString("graph_id").toLowerCase();
        graph_xml = graphProfile.getString("graph_xml");
        this.addressBook = new AddressBook(config());
        Document document = mxXmlUtils.parseXml(graph_xml);
        assert document != null;
        mxCodec codec = new mxCodec(document);
        objGraph = (mxGraphModel) codec.decode(document.getDocumentElement());
        JsonArray ja = new GraphProcess(objGraph, vertx).getJsonArrayGraph();
        if (vertx.fileSystem().existsBlocking(tempPath)) { //System.out.println(tempPath + " exist!");
            if (clearTempPath) {//Delete folder contents
                FileUtils.cleanDirectory(new File(tempPath + "/"));
                //vertx.fileSystem().deleteRecursiveBlocking(tempPath + "temp_DEBUGGER/", true);
                //vertx.fileSystem().deleteRecursiveBlocking(tempPath + "/", true);
                //System.out.println(tempPath + "temp_DEBUGGER/ deleted.");
            }
        } else {
            vertx.fileSystem().mkdirBlocking(tempPath); //System.out.println(tempPath + " created!");
        }
        timestamp = new Timestamp(System.currentTimeMillis());
        if (fixedTempPath)
            subTempPath = "_DEBUGGER/";
        else if (useTimestampInTempPath)
            subTempPath = graph_id + "_" + timestamp.getTime() + "/";
        else {
            subTempPath = graph_id + "/";
            if (vertx.fileSystem().existsBlocking(tempPath + subTempPath)) {
                FileUtils.cleanDirectory(new File(tempPath + subTempPath));
                FileUtils.deleteDirectory(new File(tempPath + subTempPath));
            }
        }
        vertx.fileSystem().mkdirBlocking(tempPath + subTempPath);
        vertx.fileSystem().writeFile(tempPath + subTempPath + "_flow.txt", Buffer.buffer(ja.encodePrettily()), result -> {
            if (!result.succeeded()) {
                System.err.println("Error in writing _flow.txt in " + tempPath + subTempPath + result.cause());
            }
        });
        this.eb.consumer(String.join(".", "vx", this.graph_id), this::processMessageFromVX6);
        this.eb.consumer(String.join(".", this.graph_id, "*"), this::processMessageFromDeployedGraph);
        JsonObject dataSources = new JsonObject();
        for (int i = 0; i < ja.size(); i++) {
            JsonObject jo = ja.getJsonObject(i);
            jo.put("graph_name", this.graph_name).put("graph_id", this.graph_id);//.put("parent_deploy_id", this.id);
            String type = jo.getString("type");
            String id = jo.getString("id");
            JsonObject data = jo.getJsonObject("data");
            DeploymentOptions options = new DeploymentOptions();
            Promise<String> pro = Promise.promise();
            futures.add(pro.future());
            switch (type.toLowerCase()) {
                case "data.source":
                    dataSources = jo.getJsonObject("data");
                    pro.complete();
                    break;
                case "process":
                    if (!data.getJsonObject("config").getBoolean("enable", true)) {
                        pro.complete();
                    } else {
                        String lang = data.getString("lang");
                        String fName = data.getString("fName");
                        String code = data.getString("code");
                        JsonArray trigger = jo.getJsonArray("Trigger");
                        JsonArray result = jo.getJsonArray("Result");
                        JsonArray error = jo.getJsonArray("Error");
                        JsonArray input = jo.getJsonArray("Input");
                        jo.put("dataSource", dataSources);
                        options.setWorker(data.getJsonObject("config").getBoolean("worker", false));
                        options.setInstances(data.getJsonObject("config").getInteger("instances", 1));
                        options.setConfig(jo);
                        FileSystem fs = vertx.fileSystem();
                        String absoluteFilePath;
                        if (lang.equalsIgnoreCase("javascript")) {
                            absoluteFilePath = subTempPath + fName + ".js";
                            Future.<Void>future(promised -> fs.writeFile(tempPath + absoluteFilePath, Buffer.buffer(code), promised))
                                    .compose(v -> {
                                        vertx.deployVerticle("js:" + tempPath + absoluteFilePath, options, pro);
                                        return pro.future();
                                    });
                        } else { // =="java"
                            absoluteFilePath = subTempPath + fName + ".java";
                            Future.<Void>future(promised -> fs.writeFile(tempPath + absoluteFilePath, Buffer.buffer(code), promised))
                                    .compose(v -> {
                                        vertx.deployVerticle("java:./" + absoluteFilePath, options, pro);
                                        return pro.future();
                                    });
                        }
                    }
                    break;
                case "switch":
                    options.setWorker(false);
                    jo.put("dataSource", dataSources);
                    options.setConfig(jo);
                    vertx.deployVerticle(new com.vx6.widget.SwitchVerticle(), options, pro);
                    break;
                case "file":
                    if (!data.getJsonObject("config").getBoolean("enable", true)) {
                        pro.complete();
                    }else {
                        options.setWorker(false);
                        jo.put("dataSource", dataSources);
                        options.setConfig(jo);
                        if (jo.getJsonArray("Input").size() == 0) {
                            vertx.deployVerticle(new com.vx6.widget.FileReadVerticle(), options, pro);
                        } else {
                            vertx.deployVerticle(new com.vx6.widget.FileWriteVerticle(), options, pro);
                        }
                    }
                    break;
                case "chart":
                    options.setWorker(false);
                    jo.put("dataSource", dataSources);
                    options.setConfig(jo);
                    vertx.deployVerticle(new com.vx6.widget.ChartVerticle(), options, pro);
                    break;
                case "webspheremq":
                    if (!data.getJsonObject("config").getBoolean("enable", true)) {
                        pro.complete();
                    }else {
                        options.setWorker(false);
                        jo.put("dataSource", dataSources);
                        options.setConfig(jo);
                        vertx.deployVerticle(new com.vx6.widget.IbmMqVerticle(), options, pro);
                    }
                    break;
                case "database":
                    if (!data.getJsonObject("config").getBoolean("enable", true)) {
                        pro.complete();
                    }else {
                        options.setWorker(false);
                        jo.put("dataSource", dataSources);
                        options.setConfig(jo);
                        vertx.deployVerticle(new com.vx6.widget.DbVerticle(), options, pro);
                    }
                    break;
                case "parquet":
                    options.setWorker(false);
                    jo.put("dataSource", dataSources);
                    options.setConfig(jo);
                    vertx.deployVerticle(new com.vx6.widget.ParquetVerticle(), options, pro);
                    break;
        /*case "buffer":
          options.setWorker(false);
          options.setConfig(jo);
          vertx.deployVerticle(new com.isc.tool.BufferVerticle(), options, res -> {
            if (res.succeeded()) {
              fu.complete();
            } else {
              fu.fail(res.cause());
              res.cause().printStackTrace();
            }
          });
          break;
        case "pattern":
          options.setWorker(false);
          options.setConfig(jo);
          vertx.deployVerticle(new com.isc.tool.PatternVerticle(), options, res -> {
            if (res.succeeded()) {
              fu.complete();
            } else {
              fu.fail(res.cause());
              res.cause().printStackTrace();
            }
          });
          break;*/
                default:
                    pro.complete();
                    break;
            }
        }
        CompositeFuture.all(futures).onComplete(ar -> {
            if (ar.succeeded()) {
                /*this.eb.consumer(String.join(".", "vx", this.graph_id), this::processMessageFromVX6);
                this.eb.consumer(String.join(".", this.graph_id, "*"), this::processMessageFromDeployedGraph);*/
                //this.eb.consumer(String.join(".", "mx", this.graph_id), this::processMessageFromMxGraph);
                startPromise.complete();
            } else {
                startPromise.fail(ar.cause());
            }
        });
    }

    private <T> void processMessageFromVX6(Message tMessage) {
        switch (tMessage.headers().get("cmd").toUpperCase()) {
            case "SET":
            case "UPDATE":
                eb.publish(String.join(".", this.graph_id, tMessage.headers().get("type"),
                                tMessage.headers().get("id"), "trigger"), tMessage.body(),
                        new DeliveryOptions().setHeaders(tMessage.headers()));
                /*eb.publish("registry", tMessage.body(), new DeliveryOptions().setHeaders(tMessage.headers()));
                tMessage.reply(tMessage.headers().get("type") + "." + tMessage.headers().get("id") + " -> (SET)");*/
                mxCell cell = (mxCell) objGraph.getCell(tMessage.headers().get("id"));
                if (cell == null)
                    tMessage.reply("nok");
                JsonObject jo = (JsonObject) cell.getData();
                if (cell.getType().equalsIgnoreCase("parquet")) {
                    jo.mergeIn(new JsonObject(tMessage.body().toString()));
                } else {
                    jo.put("setting", new JsonObject(tMessage.body().toString()));
                }
                tMessage.reply("ok");
                break;
            case "READY":
                this.eb.publish(this.graph_id, "READY", new DeliveryOptions().addHeader("cmd", "READY"));
                break;
            case "GET_GRAPH":
                mxCodec codec = new mxCodec();
                tMessage.replyAndRequest("", (Handler<AsyncResult<Message>>) ar -> {
                    if (ar.succeeded()) {
                        Future fut2 = msm.send(ar.result(), mxXmlUtils.getXml(codec.encode(objGraph)));
                        fut2.onFailure(res3 -> {
                            System.out.println(res3.toString());
                        });
                    } else {
                        ar.cause().printStackTrace();
                    }
                });
                break;
            case "CHART":
                tMessage.reply(((mxCell) objGraph.getCell(tMessage.headers().get("id"))).getMxTransient());
                break;
            /*((mxCell) objGraph.getCell(tMessage.headers().get("id"))).getValue() = {JsonObject @ 9797}"{" data ":{"
            labels ":[" P44 "," J66 "," W97 "," V98 "," H100 "," O90 "," B100 "," O75 "," F32 "," S3 "]," datasets ":[{"
            data ":[44,66,97,98,100,90,100,75,32,3]}]}," options ":{" scales ":{}," responsive ":true,"
            maintainAspectRatio ":false," resizeDelay ":0}," type ":" bar "," limit ":10}"*/
            default:
                tMessage.reply("Unidentified command!");//Or msg.fail(1,"No respond");
                break;
        }
    }

    private <T> void processMessageFromDeployedGraph(Message<T> tMessage) {
        mxCell cell;
        switch (tMessage.headers().get("cmd").toUpperCase()) {
            case "LABEL":
                cell = (mxCell) objGraph.getCell(tMessage.headers().get("id"));
                cell.setValue(tMessage.body());
                this.eb.publish(String.join(".", "vx", "mx",
                                tMessage.headers().contains("flowId") ? tMessage.headers().get("flowId") : tMessage.headers().get("graph_id")),
                        tMessage.body(), (new DeliveryOptions()).setHeaders(tMessage.headers()));
                break;
            case "CHART":
                cell = (mxCell) objGraph.getCell(tMessage.headers().get("id"));
                JsonObject data = (JsonObject) tMessage.body();
                JsonObject value = cell.getMxTransient() instanceof JsonObject ? (JsonObject) cell.getMxTransient() : new JsonObject();
                JsonObject merged = merge(value, data);
                cell.setValue(new JsonObject().put("name", "chart").put("type", merged.getString("type")
                ).put("title", tMessage.headers().get("title")));
                cell.setMxTransient(merged);
                this.eb.publish(String.join(".", "vx", "mx",
                                tMessage.headers().contains("flowId") ? tMessage.headers().get("flowId") : tMessage.headers().get("graph_id")),
                        data.containsKey("type") ? merged : data,
                        (new DeliveryOptions()).setHeaders(tMessage.headers()));
                break;
        }
    }

    private void processMessageFromMxGraph(Message msg) {
        //This method is for receiving dynamic settings from any widget of mxEditor and publish routed it to the related verticle after registering it contents.
        //If message cmd == "set" then set it in shared-setting and then notify related
        //verticle to update its setting.
        if (msg.headers().get("cmd").equalsIgnoreCase("set")) {
            eb.send(String.join(".", this.graph_id, msg.headers().get("type"),
                            msg.headers().get("id"), "trigger"), msg.body(),
                    new DeliveryOptions().setHeaders(msg.headers()));
            eb.send("registry", msg.body(), new DeliveryOptions().setHeaders(msg.headers()));
            eb.publish(String.join(".", "vx", this.graph_id), msg.body(), new DeliveryOptions().setHeaders(msg.headers()));
        } else if (msg.headers().get("cmd").equalsIgnoreCase("update")) {
            eb.send(String.join(".", this.graph_id, msg.headers().get("type"), msg.headers().get("id"), "trigger"), msg.body(), new DeliveryOptions().setHeaders(msg.headers()));
            eb.send("registry", msg.body(), new DeliveryOptions().setHeaders(msg.headers()));
            eb.publish(String.join(".", "vx", this.graph_id), msg.body(), new DeliveryOptions().setHeaders(msg.headers()));
        }
        //TODO route message to related verticle base on message headers contents.
        msg.reply("Unidentified command!");//Or msg.fail(1,"No respond");
    }

    private static JsonObject merge(JsonObject val, JsonObject cha) {
        JsonObject value = val.copy(), chart = cha.copy();
        if (!value.containsKey("data"))
            value.put("data", new JsonObject());
        if (!value.getJsonObject("data").containsKey("labels"))
            value.getJsonObject("data").put("labels", new JsonArray());
        if (!value.getJsonObject("data").containsKey("datasets"))
            value.getJsonObject("data").put("datasets", new JsonArray());
        if (chart.containsKey("type")) {
            return value.mergeIn(chart, true);
        } else {
            //Add and Merge
            JsonObject merged = new JsonObject().mergeIn(value.copy(), true).mergeIn(chart, true);
            if (chart.containsKey("data")) {
                if (chart.getJsonObject("data").containsKey("labels")) {
                    merged.getJsonObject("data").put("labels", value.getJsonObject("data").getJsonArray("labels")
                            .copy().addAll(chart.getJsonObject("data").getJsonArray("labels")));
                }
                if (chart.getJsonObject("data").containsKey("datasets")
                        && chart.getJsonObject("data").getJsonArray("datasets").size() > 0) {
                    JsonArray datasets = chart.getJsonObject("data").getJsonArray("datasets");
                    merged.getJsonObject("data").put("datasets", new JsonArray());
                    for (int i = 0; i < datasets.size(); i++) {
                        if (value.getJsonObject("data").getJsonArray("datasets").size() > i) {
                            JsonObject valueDataset = value.getJsonObject("data").getJsonArray("datasets").getJsonObject(i);
                            JsonObject chartDataset = datasets.getJsonObject(i);
                            JsonObject mergedDataset = new JsonObject().mergeIn(valueDataset.copy(), false)
                                    .mergeIn(chartDataset.copy(), false);
                            if (chartDataset.containsKey("data")) {
                                mergedDataset.put("data", valueDataset.getJsonArray("data", new JsonArray())
                                        .addAll(chartDataset.getJsonArray("data", new JsonArray())));
                            } else if (valueDataset.containsKey("data")) {
                                mergedDataset.put("data", valueDataset.getJsonArray("data"));
                            }
                            merged.getJsonObject("data").getJsonArray("datasets").add(mergedDataset);
                        } else {
                            merged.getJsonObject("data").getJsonArray("datasets").add(datasets.getJsonObject(i));
                        }
                        //dataset.getJsonObject(i)
                    }
                }
            }
            return limit(merged);
        }
    }

    private static JsonObject limit(JsonObject merged) {
        if (merged.containsKey("limit")) {
            int limit = merged.getInteger("limit");
            JsonArray labels = merged.getJsonObject("data").getJsonArray("labels");
            while (labels.size() > limit) {
                labels.remove(0);//Remove oldest item
            }
            JsonArray datasets = merged.getJsonObject("data").getJsonArray("datasets");
            for (int i = 0; i < datasets.size(); i++) {
                while (datasets.getJsonObject(i).getJsonArray("data").size() > limit) {
                    datasets.getJsonObject(i).getJsonArray("data").remove(0);//Remove oldest item
                }
            }
        }
        return merged;
    }
}
