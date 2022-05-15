package com.vx6.worker;

import com.mxgraph.util.mxXmlUtils;
import com.vx6.master.AddressBook;
import com.vx6.tools.GraphProfile;
import com.vx6.tools.MultipartStringMessage;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
//import io.vertx.core.impl.future.PromiseImpl;
import io.vertx.core.json.JsonObject;
import org.w3c.dom.Document;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

//A media settled between MX and VX system and route message
// to appropriate verticle in charge of related flow.
public class VXMGateway extends AbstractVerticle {
    private String id, parentID;
    private EventBus eb;
    private static final String tempPath = "./temp/";
    private static String subTempPath;
    private Timestamp timestamp;
    private List<Future> futures = new ArrayList<>();
    private boolean fixedTempPath = true;
    private AddressBook addressBook;
    private MultipartStringMessage msm;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        this.eb = vertx.eventBus();
        msm = new MultipartStringMessage(vertx, config().getJsonObject("multiChunksMessage"));
        this.eb.consumer("registry.vx", this::processRegistry);
        this.eb.consumer("mx.vx", this::processMessage);
        this.eb.consumer("vx", this::sendOutMessage);
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
            GraphProfile graphProfile = new GraphProfile((JsonObject) msg.body());
            Future<String> fut = msm.get(msg);
            fut.onSuccess(res -> {
                graphProfile.put("graph_xml", res);
                deployVerticle(graphProfile, false);
            });
            fut.onFailure(res -> {
                res.getCause().printStackTrace();
            });
        }
    }

    private void processMessage(Message msg) {
        final String graph_name, graph_id;
        //GraphProfile gp;
        futures.clear();
        graph_id = msg.headers().get("uid").toLowerCase();
        graph_name = msg.headers().get("name");
        switch (msg.headers().get("cmd").toLowerCase()) {
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
                //eb.send(String.join(".", "vx", graph_id), msg);
                break;
            case "deploy":
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
                            if (msg.body() != null && msg.body().toString().trim().length() > 0) {
                                Future<String> fut = msm.get(msg);
                                fut.onSuccess(res -> {
                                    graphProfile.put("graph_xml", res);
                                    deployVerticle(graphProfile, true);
                                });
                                fut.onFailure(res -> {
                                    res.getCause().printStackTrace();
                                });
                            } else {
                                eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "get_graph")
                                        .addHeader("uid", graph_id), arr -> {
                                    if (arr.succeeded()) {
                                        Future<String> fut = msm.get(arr.result());
                                        fut.onSuccess(res -> {
                                            graphProfile.put("graph_xml", res);
                                            deployVerticle(graphProfile, false);
                                        });
                                        fut.onFailure(res -> {
                                            res.getCause().printStackTrace();
                                        });
                                    } else {
                                        arr.cause().printStackTrace();
                                    }
                                });
                            }
                        }
                    } else {
                        //register new one.
                        Future<String> fut = msm.get(msg);
                        fut.onSuccess(res -> {
                            GraphProfile gpf = new GraphProfile();
                            gpf.put("graph_name", graph_name).put("graph_id", graph_id).put("graph_xml", res);
                            deployVerticle(gpf, true);
                            //System.out.println(String.format("ERROR! Graph \"%s\" has no valid xml.", graph_name));
                        });
                        fut.onFailure(res -> {
                            res.getCause().printStackTrace();
                        });
                    }
                });
                break;
            case "redeploy":
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
                                if (msg.body() != null && msg.body().toString().trim().length() > 0) {
                                    Future<String> fut0 = msm.get(msg);
                                    fut0.onSuccess(res0 -> {
                                        graphProfile.put("graph_xml", res0);
                                        deployVerticle(graphProfile, true);
                                    });
                                    fut0.onFailure(res0 -> {
                                        res0.getCause().printStackTrace();
                                    });
                                } else {
                                    eb.request("registry", graph_id, new DeliveryOptions().addHeader("cmd", "get_graph")
                                            .addHeader("uid", graph_id), arr -> {
                                        if (arr.succeeded()) {
                                            Future<String> fut0 = msm.get(arr.result());
                                            fut0.onSuccess(res0 -> {
                                                graphProfile.put("graph_xml", res0);
                                                deployVerticle(graphProfile, false);
                                            });
                                            fut0.onFailure(res0 -> {
                                                res0.getCause().printStackTrace();
                                            });
                                        } else {
                                            arr.cause().printStackTrace();
                                        }
                                    });
                                }
                            });
                            fut.onFailure(res -> {
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
            case "save":
                saveGraph(msg);
                break;
            default:
                break;
        }
    }

    private void saveGraph(Message msg) {
        String graph_id = msg.headers().get("uid").toLowerCase();
        String graph_name = msg.headers().get("name");
        Document document = mxXmlUtils.parseXml(msg.body().toString());
        if (document != null) {
            GraphProfile gpf = new GraphProfile();
            gpf.put("graph_name", graph_name).put("graph_id", graph_id).put("graph_xml", msg.body().toString());
            eb.request("registry", gpf, new DeliveryOptions().setHeaders(msg.headers().set("cmd", "save")), reg -> {
                if (reg.succeeded())
                    msg.reply(reg.result().body());
                else
                    msg.fail(-1, reg.cause().getMessage());
            });
        } else {
            msg.fail(10, String.format("Graph \"%s\" has no valid xml.", graph_name));
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
                if (!newGraphXml)
                    graphProfile.remove("graph_xml");
                eb.request("registry", graphProfile, new DeliveryOptions().addHeader("cmd", "add_or_update"),
                        res1 -> {
                            if (res1.succeeded()) {
                                if (graphProfile.getGraph_xml() != null) {
                                    Future<String> fut2 = msm.send(res1.result(), graphProfile.getGraph_xml());
                                    fut2.onFailure(res2 -> {
                                        res2.getCause().printStackTrace();
                                    });
                                }
                            } else {
                                res1.cause().printStackTrace();
                            }
                        });

                //eb.send("registry", graphProfile, new DeliveryOptions().addHeader("cmd", "add_profile"));
                System.out.printf("Graph Deployed! Name:\"%s\" id:\"%s\" Deploy_id: \"%s\"%n",
                        graphProfile.getGraph_name(), graphProfile.getGraph_id(), res.result());
                graphProfile.addModification();
                //msg.reply(graphProfile.getGraph_xml(), new DeliveryOptions().setHeaders(graphProfile.toMultiMap().remove("graph_xml")));
                /*msg.replyAndRequest(graphProfile.getGraph_xml(),
                        new DeliveryOptions().setHeaders(graphProfile.toMultiMap().remove("graph_xml")),
                        (Handler<AsyncResult<Message<String>>>) res1 -> {
                            if (res1.succeeded()) {
                                String r = res1.result().body();
                                System.out.println(r);
                            } else {

                            }
                        });*/
                //Let all know that the flow is now ready to start.
                eb.publish("vx.mx", "DEPLOYED", new DeliveryOptions()
                        .setHeaders(graphProfile.toSimpleMultiMap())
                        .addHeader("cmd", "status")
                        .addHeader("value", "Deployed"));
                eb.publish(String.join(".", "vx", graphProfile.getGraph_id()), "READY",
                        new DeliveryOptions().setHeaders(graphProfile.toSimpleMultiMap()).addHeader("cmd", "ready"));
                //.addHeader("profile", graphProfile.toSimpleJsonObject().toString()));
            } else {
                System.out.println(String.format("Deployment failed! graph_name: %s graph_id %s%n",
                        graphProfile.getGraph_name(), graphProfile.getGraph_id()) + res.cause());
                res.cause().printStackTrace();
                //msg.fail(3, "Deployment failed!");
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
          /*if (msg != null)
            msg.reply("graph_undeployed", new DeliveryOptions().addHeader("profile", graphProfile.toSimpleJsonObject().toString()));*/
                    //Let all know that the flow is now ready to start.
                    eb.publish(String.join(".", graphProfile.getGraph_id(), "command"), "graph_undeployed",
                            new DeliveryOptions().setHeaders(graphProfile.toSimpleMultiMap()));
                    //.addHeader("profile", graphProfile.toSimpleJsonObject().toString()));
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
                        JsonObject jo = new JsonObject(reg.result().body().toString());
                        msg.replyAndRequest("", new DeliveryOptions()
                                        //.addHeader("graph_id", jo.getString("graph_id").toLowerCase())
                                        //.addHeader("graph_name", jo.getString("graph_name"))
                                        .setHeaders(new GraphProfile(jo).toSimpleMultiMap()),
                                (Handler<AsyncResult<Message>>) res0 -> {
                                    if (res0.succeeded()) {
                                        if (jo.getBoolean("active")) {
                                            this.eb.request(String.join(".", "vx", jo.getString("graph_id")),
                                                    msg.body(),
                                                    new DeliveryOptions().setHeaders(msg.headers()),
                                                    dg -> {
                                                        if (dg.succeeded()) {
                                                            Future<String> fut = msm.get(dg.result());
                                                            fut.onComplete(res -> {
                                                                jo.put("graph_xml", res.result());
                                                                msm.send(res0.result(), res.result(), new GraphProfile(jo));
                                                            });
                                                            fut.onFailure(res -> {
                                                                res.getCause().printStackTrace();
                                                            });
                                                        }
                                                    });
                                        } else {
                                            Future<String> fut = msm.get(reg.result());
                                            fut.onComplete(res -> {
                                                jo.put("graph_xml", res.result());
                                                msm.send(res0.result(), res.result(), new GraphProfile(jo));
                                            });
                                            fut.onFailure(res -> {
                                                res.getCause().printStackTrace();
                                            });
                                        }
                                    } else {
                                        res0.cause().printStackTrace();
                                    }
                                });
                    } else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void getGraphProxy(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers()), reg -> {
                    if (reg.succeeded()) {
                        JsonObject jo = new JsonObject(reg.result().body().toString());
                        msg.replyAndRequest("", new DeliveryOptions()
                                        .setHeaders(new GraphProfile(jo).toMultiMap().remove("graph_xml")),
                                (Handler<AsyncResult<Message>>) res0 -> {
                                    if (res0.succeeded()) {
                                        Future<String> fut = msm.get(reg.result());
                                        fut.onComplete(res -> {
                                            jo.put("graph_xml", res.result());
                                            msm.send(res0.result(), res.result(), new GraphProfile(jo));
                                        });
                                        fut.onFailure(res -> {
                                            res.getCause().printStackTrace();
                                        });
                                    } else {
                                        res0.cause().printStackTrace();
                                    }
                                });
                    } else
                        msg.fail(-1, reg.result().body().toString());
                });
    }

    private void getGraphList(Message msg) {
        this.eb.request("registry",
                msg.body(), new DeliveryOptions().setHeaders(msg.headers())/*.addHeader("cmd", "get_graph_list")*/, reg -> {
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
