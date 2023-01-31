package com.vx6.worker;

import com.vx6.tools.GraphProfile;
import com.vx6.worker.test.SqlServer;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class VXMGateway extends AbstractVerticle {
    private EventBus eb;
    private List<Future> futures = new ArrayList<>();
    private LocalMap<String, JsonObject> graphMap;
    private static final Logger log = LoggerFactory.getLogger(VXMGateway.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        graphMap = sharedData.getLocalMap("_FORM_GRAPH_MAP");
        this.eb = vertx.eventBus();
        this.eb.consumer("registry.vx", this::processRegistry);
        this.eb.consumer("mx.vx", this::processMessage);
        this.eb.consumer("vx", this::sendOutMessage);
        log.info("Starting verticle {" + this + "}");
        startPromise.complete();
    }

    private <T> void sendOutMessage(Message<T> tMessage) {
        this.eb.publish(String.join(".", "vx", "mx",
                        tMessage.headers().contains("flowId") ? tMessage.headers().get("flowId") : tMessage.headers().get("graph_id")),
                tMessage.body(), (new DeliveryOptions()).setHeaders(tMessage.headers()));
    }

    @Override
    public void stop() throws Exception {
    }

    private void processRegistry(Message msg) {
        if ("load".equals(msg.headers().get("cmd"))) {
            JsonObject body = (JsonObject) msg.body();
            JsonObject graph = graphMap.get(body.getString("graph"));
            String garphXml = graph.getString("graph");
            GraphProfile graphProfile = new GraphProfile(body);
            graphProfile.put("graph_xml", garphXml);
            deployVerticle(graphProfile, false);
        }
    }

    private void processMessage(Message msg) {
        String cmd = "";
        if (msg.headers().contains("cmd"))
            cmd = msg.headers().get("cmd");
        else if (msg.body() instanceof String)
            cmd = msg.body().toString();
        else if (msg.body() instanceof JsonObject)
            cmd = ((JsonObject) msg.body()).getString("cmd");
        JsonObject body = new JsonObject();
        if (msg.body() instanceof JsonObject)
            body = (JsonObject) msg.body();
        final String graph_name, graph_id;
        futures.clear();
        graph_id = msg.headers().contains("uid") ? msg.headers().get("uid").toLowerCase() : body.getString("uid");
        graph_name = msg.headers().contains("name") ? msg.headers().get("name") : body.getString("name");
        switch (cmd.toLowerCase()) {
            case "chart":
                eb.request(String.join(".", "vx", graph_id),
                        msg.body(),
                        new DeliveryOptions().setHeaders(msg.headers()), ar -> {
                            if (ar.succeeded()) {
                                msg.reply(ar.result().body());
                            }
                        });
                break;
            case "set":
                eb.request(String.join(".", "vx", graph_id),
                        msg.body(),
                        new DeliveryOptions().setHeaders(msg.headers()), ar -> {
                            if (ar.succeeded()) {
                                msg.reply(ar.result().body());
                            }
                        });
                break;
            case "deploy":
                JsonObject finalBody = body;
                eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "query"), ar -> {
                    GraphProfile graphProfile;
                    if (ar.succeeded()) {//There is already a graph with such id
                        graphProfile = new GraphProfile((JsonObject) ar.result().body());
                        if (graphProfile.isActive()) {
                            //Tell firstly to undeploy it...
                            msg.fail(10, String.format("Graph \"%s\" is already activated! First undeploy it.",
                                    graphProfile.getGraph_name()));
                        } else {
                            //Remove old name and if there is new xml.
                            graphProfile.setGraph_name(graph_name);
                            if (finalBody.containsKey("graph") && finalBody.getString("graph").length() == 36) {
                                var graph = graphMap.get(finalBody.getString("graph"));
                                graphProfile.put("graph_xml", graph.getString("graph"));
                                deployVerticle(graphProfile, true);
                            } else {
                                JsonObject graph = graphMap.get(graphProfile.getString("graph"));
                                String graph_xml = graph.getString("graph");
                                graphProfile.put("graph_xml", graph_xml);
                                deployVerticle(graphProfile, false);
                            }
                            msg.reply("deployed");
                        }
                    } else {
                        //register new one.
                        var graph = graphMap.get(finalBody.getString("graph"));
                        String graph_xml = graph.getString("graph");
                        GraphProfile gpf = new GraphProfile();
                        gpf.put("graph_name", graph_name).put("graph_id", graph_id).put("graph_xml", graph_xml);
                        deployVerticle(gpf, true);
                        msg.reply("deployed");
                    }
                });
                break;
            case "redeploy":
                JsonObject finalBody1 = body;
                eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "query"), ar -> {
                    GraphProfile graphProfile;
                    if (ar.succeeded()) {//There is a graph with such id so check if it is active or not then undeploy it.
                        graphProfile = new GraphProfile((JsonObject) ar.result().body());
                        if (graphProfile.isActive()) {
                            //Undeploy it...
                            Promise promise = undeployVerticle(graphProfile);
                            Future fut = promise.future();
                            fut.onSuccess(res -> {
                                eb.publish("vx.mx", "UNDEPLOYED", new DeliveryOptions()
                                        .setHeaders(graphProfile.toSimpleMultiMap())
                                        .addHeader("cmd", "status")
                                        .addHeader("value", "Undeployed"));
                                if (finalBody1.containsKey("graph")) {
                                    var graph = graphMap.get(finalBody1.getString("graph"));
                                    String graph_xml = graph.getString("graph");
                                    graphProfile.put("graph_xml", graph_xml);
                                    deployVerticle(graphProfile, true);
                                } else {
                                    var graph = graphMap.get(graphProfile.getString("graph"));
                                    String graph_xml = graph.getString("graph");
                                    graphProfile.put("graph_xml", graph_xml);
                                    deployVerticle(graphProfile, false);
                                }
                                msg.reply("redeployed");
                            });
                            fut.onFailure(res -> {
                                msg.fail(7, String.format("Failed to undeploy Graph \"%s\"", graphProfile.getGraph_name()));
                                System.out.println("Undeploying failed!!!!");
                            });
                        } else {
                            msg.fail(10, String.format("Graph \"%s\" is not active!", graphProfile.getGraph_name()));
                        }
                    } else {
                        msg.fail(6, String.format("Graph \"%s\" was not found! id: %s", graph_name, graph_id));
                    }
                });
                break;
            case "undeploy":
                eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "query"), ar -> {
                    GraphProfile graphProfile;
                    if (ar.succeeded()) {//There is a graph with such id so check if it is active or not then undeploy it.
                        graphProfile = new GraphProfile((JsonObject) ar.result().body());
                        if (graphProfile.isActive()) {
                            //Undeploy it...
                            undeployVerticle(graphProfile, msg);
                            eb.publish("vx.mx", "UNDEPLOYED", new DeliveryOptions()
                                    .setHeaders(graphProfile.toSimpleMultiMap())
                                    .addHeader("cmd", "status")
                                    .addHeader("value", "Undeployed"));
                        } else {
                            msg.fail(10, String.format("Graph \"%s\" is not active!", graphProfile.getGraph_name()));
                        }
                    } else {
                        msg.fail(6, String.format("Graph \"%s\" was not found! id: %s", graph_name, graph_id));
                    }
                });
                break;
            case "list":
                getGraphList(msg);
                break;
            case "set_graph_name":
                setGraph_name(msg);
                break;
            case "query":
                eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "query"), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.result().body().toString());
                });
                break;
            case "graphs_detail":
                getGraphsDetail(msg);
                break;
            case "graph_detail":
                getGraphDetail(msg);
                break;
            case "get_graph":
                getGraph(msg);
                break;
            case "remove":
                removeGraph(msg);
                break;
            default:
                break;
        }
    }

    private void removeGraph(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.cause().getMessage());
                });
    }

    private void getGraphsDetail(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void getGraphDetail(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void deployVerticle(GraphProfile graphProfile, boolean newGraphXml) {
        DeploymentOptions opt = new DeploymentOptions();
        JsonObject jo = new JsonObject();
        jo.put("graphProfile", graphProfile).put("config", config());
        opt.setWorker(true).setConfig(jo);
        vertx.deployVerticle(new DeployGraph(), opt, res -> {
            if (res.succeeded()) {
                graphProfile.setDeploy_id(res.result());
                graphProfile.setActive(true);
                String graph_xml = (String) graphProfile.remove("graph_xml");
                if (newGraphXml) {
                    String uuid = UUID.randomUUID().toString();
                    JsonObject graph = graphProfile.copy().put("time", new Date().toInstant()).put("graph", graph_xml);
                    graphMap.put(uuid, graph);
                    graphProfile.put("graph", uuid);
                }
                eb.send("registry", graphProfile, new DeliveryOptions().addHeader("cmd", "add_or_update"));
                System.out.printf("Graph Deployed! Name:\"%s\" id:\"%s\" Deploy_id: \"%s\"%n",
                        graphProfile.getGraph_name(), graphProfile.getGraph_id(), res.result());
                graphProfile.addModification();
                //Let all know that the flow is now ready to start.
                eb.publish("vx.mx", "DEPLOYED", new DeliveryOptions()
                        .setHeaders(graphProfile.toSimpleMultiMap())
                        .addHeader("cmd", "status")
                        .addHeader("value", "Deployed"));
                eb.publish(String.join(".", "vx", graphProfile.getGraph_id()), "READY",
                        new DeliveryOptions().setHeaders(graphProfile.toSimpleMultiMap()).addHeader("cmd", "ready"));
            } else {
                System.out.println(String.format("Deployment failed! graph_name: %s graph_id %s%n",
                        graphProfile.getGraph_name(), graphProfile.getGraph_id()) + res.cause());
                res.cause().printStackTrace();
                eb.publish("vx.mx", "DEPLOY FAILED", new DeliveryOptions()
                        .setHeaders(graphProfile.toSimpleMultiMap())
                        .addHeader("cmd", "status")
                        .addHeader("value", "Deployment failed"));
            }
        });
    }

    private void undeployVerticle(GraphProfile graphProfile, Message msg) {
        graphProfile.remove("graph_xml");
        String id = graphProfile.getString("deploy_id");
        if (vertx.deploymentIDs().contains(id)) {
            vertx.undeploy(id, res -> {
                if (res.succeeded()) {
                    System.out.printf("Graph Undeployed! Name:\"%s\" id:\"%s\" Deploy_id: \"%s\"%n",
                            graphProfile.getGraph_name(), graphProfile.getGraph_id(), id);
                    graphProfile.put("deploy_id", "");
                    graphProfile.setActive(false);
                    eb.send("registry", graphProfile, new DeliveryOptions().addHeader("cmd", "add_or_update"));
                    if (msg != null)
                        msg.reply("graph_undeployed", new DeliveryOptions()
                                .setHeaders(graphProfile.toSimpleMultiMap()));
                    //.addHeader("profile", graphProfile.toSimpleJsonObject().toString()));
                    //Let all know that the flow is now ready to start.
                    eb.publish(String.join(".", graphProfile.getGraph_id(), "command"),
                            "graph_undeployed", new DeliveryOptions()
                                    .setHeaders(graphProfile.toSimpleMultiMap()));
                    //.addHeader("profile", graphProfile.toSimpleJsonObject().toString()));
                } else {
                    System.out.println(String.format("Undeploying failed! graph_name: %s graph_id %s%n",
                            graphProfile.getGraph_name(), graphProfile.getGraph_id()) + res.cause());
                    res.cause().printStackTrace();
                }
            });
        } else {
            if (msg != null)
                msg.fail(3, String.format("This graph_id: %s is not deployed!", id));
        }
    }

    private Promise undeployVerticle(GraphProfile graphProfile) {
        Promise pro = Promise.promise();
        graphProfile.remove("graph_xml");
        String id = graphProfile.getString("deploy_id");
        if (vertx.deploymentIDs().contains(id)) {
            vertx.undeploy(id, res -> {
                if (res.succeeded()) {
                    System.out.printf("Graph Undeployed! Name:\"%s\" id:\"%s\" Deploy_id: \"%s\"%n",
                            graphProfile.getGraph_name(), graphProfile.getGraph_id(), id);
                    graphProfile.put("deploy_id", "");
                    graphProfile.setActive(false);
                    eb.send("registry", graphProfile, new DeliveryOptions().addHeader("cmd", "add_or_update"));
                    //Let all know that the flow is now ready to start.
                    eb.publish(String.join(".", graphProfile.getGraph_id(), "command"), "graph_undeployed",
                            new DeliveryOptions().setHeaders(graphProfile.toSimpleMultiMap()));
                    pro.complete();
                } else {
                    System.out.println(String.format("Undeploying failed! graph_name: %s graph_id %s%n",
                            graphProfile.getGraph_name(), graphProfile.getGraph_id()) + res.cause());
                    res.cause().printStackTrace();
                    pro.fail(res.cause());
                }
            });
        } else {
            pro.fail(new Exception("Deployment id not exists!"));
        }
        return pro;
    }

    private void getGraph(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded()) {
                        msg.reply(reg.result().body());
                    } else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void getGraphList(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void setGraph_name(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded())
                        msg.reply(reg.result().body());
                    else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void unsuccessfulUndeploy(Message msg, String graph_name, Promise promise, AsyncResult<Void> res) {
        System.out.println("Could not undeploy graph: " + graph_name);
        res.cause().printStackTrace();
        promise.fail(res.cause());
        DeliveryOptions opt = new DeliveryOptions();
        opt.addHeader("cause", res.cause().getMessage()).addHeader("stack_trace", exceptionAsString(res.cause()));
        msg.reply("undeploy_failed", opt);
    }

    private void persistTheGraphInFile(Message msg) {
        String graph_name = msg.headers().get("graph_name");
        File file = new File("./webroot/graphs/" + graph_name + ".xml");
        if (vertx.fileSystem().existsBlocking(file.getAbsolutePath())) {
            vertx.fileSystem().deleteBlocking(file.getAbsolutePath());
        }
        vertx.fileSystem().writeFileBlocking(file.getAbsolutePath(), Buffer.buffer(msg.body() + ""));
    }

    private String exceptionAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    private void routeMessage(Message msg) {
        //Route settings to corresponding verticle
        eb.request(String.join(".", msg.headers().get("graph_id"), msg.headers().get("type"), msg.headers().get("id")),
                msg.body(),
                new DeliveryOptions().setHeaders(msg.headers()),
                ar -> {
                    if (ar.succeeded()) {
                        msg.reply("Received reply: " + ar.result().body());
                    } else {
                        msg.reply(ar.cause());
                    }
                });
    }
}
