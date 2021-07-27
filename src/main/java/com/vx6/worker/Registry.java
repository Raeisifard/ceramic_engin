package com.vx6.worker;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxXmlUtils;
import com.vx6.tools.DataRoot;
import com.vx6.tools.GraphProfile;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageManager;

public class Registry extends AbstractVerticle {
    private final DataRoot root = new DataRoot();
    //private final JsonObject root = new JsonObject();
    private EmbeddedStorageManager storageManager;
    private static final String data_dir = "data";
    private static final String backup_dir = "data_backup";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        vertx.eventBus().consumer("registry", this::processMessage);
        // configuring the database via .ini file instead of API. Here the directory and the thread count.
        final Configuration configuration = Configuration.LoadIni(
                Registry.class.getResource("/microstream/storage.ini"));
        this.storageManager = configuration.createEmbeddedStorageFoundation().createEmbeddedStorageManager(root).start();
        if (storageManager.root() == null) {
            storageManager.storeRoot();
        }
        /*this.storageManager = Configuration.Default()
                .setBaseDirectoryInUserHome(data_dir)
                //.setBackupDirectory(backup_dir)
                .setChannelCount(4)
                .createEmbeddedStorageFoundation()
                .createEmbeddedStorageManager(root).start();

        if (((DataRoot)storageManager.root()).getRevision() == 0) {
            storageManager.storeRoot();
        }*/
        if (config().getBoolean("auto_deploy")) {
            root.getGraphProfiles().forEach((graph_id, graphProfile) -> {
                if (graphProfile.isActive()) {
                    graphProfile.setActive(false);
                    vertx.eventBus().request("mx.vx", "", new DeliveryOptions().addHeader("cmd", "deploy").addHeader("name", graphProfile.getGraph_name()).addHeader("uid", graphProfile.getGraph_id()), ar -> {
                        if (ar.succeeded()) {
                            //System.out.printf("Graph Deployed! Name:\"%s\" id:\"%s\" Deploy_id: \"%s\"%n", graphProfile.getGraph_name(), graphProfile.getGraph_id(), ar.result());
                        } else {
                            System.out.println(String.format("Deployment failed! graph_name: %s graph_id %s%n", graphProfile.getGraph_name(), graphProfile.getGraph_id()) + ar.cause());
                            ar.cause().printStackTrace();
                        }
                    });
                }
            });
        }
        startPromise.complete();
    }

    private <T> void processMessage(Message<T> tMessage) {
        String cmd = tMessage.headers().get("cmd");
        GraphProfile gp;
        switch (cmd.toLowerCase()) {
            case "query"://Simple graph profile
                JsonObject jo = query(tMessage.body().toString());
                if (jo != null)
                    tMessage.reply(jo);
                else
                    tMessage.fail(5, String.format("Graph id: \"%s\" not exist!", tMessage.body().toString()));
                break;
            case "add_or_update":
                gp = new GraphProfile((JsonObject) tMessage.body());
                if (gp.getGraph_xml() == null)
                    gp.addModification();
                else
                    gp.addRevision();
                root.addOrReplace(gp);
                storageManager.store(root.getGraphProfiles());
                storageManager.store(root.graphModels);
                tMessage.reply(root.getProfile(gp.getGraph_id()).toSimpleJsonObject());
                break;
            case "register":
            case "add_profile":
                register(tMessage);
                break;
            case "unregister":
            case "delete_profile":
                unregister(tMessage);
                break;
            case "remove":
                removeGraphId(tMessage);
                break;
            case "save":
                String graph_id = tMessage.headers().get("uid").toLowerCase();
                String graph_name = tMessage.headers().get("name");
                gp = new GraphProfile((JsonObject) tMessage.body());
                gp.addRevision();
                JsonObject queryJo = query(graph_id);
                if (queryJo != null){
                    gp.setDeploy_id(queryJo.getString("deploy_id", "THERE_IS_NO_DEPLOY_ID"));
                    gp.setActive(queryJo.getBoolean("active"));
                    gp.addModification();
                }else{
                    gp.setDeploy_id("NEVER_BEEN_DEPLOYED_YET");
                    gp.setActive(false);
                }
                root.addOrReplace(gp);
                storageManager.store(root.getGraphProfiles());
                storageManager.store(root.graphModels);
                tMessage.reply(root.getProfile(gp.getGraph_id()).toSimpleJsonObject());
                break;
            case "set":
                set2MxGraphModel(tMessage);
                break;
            case "update":
                set2MxGraphModel(tMessage);
                break;
            case "list":
                tMessage.reply(getGraphList(tMessage));
                break;
            case "set_graph_name":
                setGraphName(tMessage);
                break;
            case "graph_detail":
                tMessage.reply(getGraphDetail(tMessage));
                break;
            case "graphs_detail":
                tMessage.reply(getGraphDetail(tMessage));
                break;
            case "get_graph":
                getGraph(tMessage);
                break;
            default:
                tMessage.fail(-1, "unknown");
                break;
        }
    }

    private void setGraphName(Message<?> tMessage) {
        String graph_id = tMessage.headers().get("uid").toLowerCase();
        String graph_name = tMessage.headers().get("name");
        try {
            GraphProfile gp = new GraphProfile((JsonObject) root.getGraphProfiles().get(graph_id).copy());
            gp.setGraph_name(graph_name);
            root.getGraphProfiles().replace(graph_id, gp);
            mxCell cell = (mxCell) root.getGraphModel(graph_id).getCell("0");
            cell.setName(graph_name);
            storageManager.store(root.getGraphProfiles());
            storageManager.store(cell);
            //storageManager.store(root.getGraphModel(graph_id));
            //storageManager.store(root.getGraphProfiles());
            //storageManager.store(root.graphModels);
            tMessage.reply(String.format("Changing name of graph_id: %s to %s was successful!", graph_id, graph_name));
        } catch (Exception e) {
            tMessage.fail(1, String.format("Chould not change name of graph_id: %s to %s!", graph_id, graph_name));
            e.printStackTrace();
        }
    }

    private JsonObject query(String graph_id) {
        GraphProfile gp = root.getProfile(graph_id);
        if (gp != null)
            return gp.toSimpleJsonObject();
        else
            return null;
    }

    private <T> Object set2MxGraphModel(Message<T> tMessage) {
        mxGraphModel gm = root.getGraphModel(tMessage.headers().get("uid").toLowerCase());
        mxCell cell = (mxCell) gm.getCell(tMessage.headers().get("id"));
        if (cell == null)
            return "nok";
        JsonObject jo = (JsonObject) cell.getData();
        jo.put("setting", new JsonObject(tMessage.body().toString()));
        return "ok";
    }

    private <T> Object updateMxGraphModel(Message<T> tMessage) {
        mxGraphModel gm = root.getGraphModel(tMessage.headers().get("uid").toLowerCase());
        mxCell cell = (mxCell) gm.getCell(tMessage.headers().get("id"));
        JsonObject jo = (JsonObject) cell.getData();
        jo.put("setting", jo.getJsonObject("setting").mergeIn((JsonObject) tMessage.body()));
        return "ok";
    }

    private void register(Message<?> tMessage) {
        GraphProfile gp = (GraphProfile) tMessage.body();
        try {
            root.addProfile(gp);
            storageManager.store(root.getGraphProfiles());
            storageManager.store(root.graphModels);
            tMessage.reply(root.getProfile(gp.getGraph_id()).toSimpleJsonObject());
        } catch (Exception e) {
            tMessage.fail(5, e.getMessage());
        }
    }

    private void unregister(Message<?> tMessage) {
        GraphProfile gp = (GraphProfile) tMessage.body();
        try {
            root.addProfile(gp);
            storageManager.store(root.getGraphProfiles());
            storageManager.store(root.graphModels);
            tMessage.reply(root.getProfile(gp.getGraph_id()).toSimpleJsonObject());
        } catch (Exception e) {
            tMessage.fail(5, e.getMessage());
        }
    }

    private <T> void removeGraphId(Message<T> tMessage) {
        String graphId = tMessage.body().toString();
        String graphName = tMessage.headers().get("name");
        GraphProfile gp = root.getProfile(graphId);
        if (gp == null) {
            tMessage.fail(3, "Graph id not exist");
        } else {
            if (gp.isActive()) {
                tMessage.fail(1, "Can't remove an active graph");
            } else {
                try {
                    root.removeProfile(graphId);
                    storageManager.store(root.getGraphProfiles());
                    storageManager.store(root.graphModels);
                    tMessage.reply("Graph " + graphName + "(" + graphId + ") removed");
                } catch (Exception e) {
                    tMessage.fail(2, e.getMessage());
                }
            }
        }
    }

    private <T> JsonObject getGraphList(Message<T> tMessage) {
        JsonArray ja = new JsonArray();
        JsonObject jo = new JsonObject();
        root.getGraphProfiles().forEach((id, gp) -> {
            ja.add(gp.toSimpleJsonObject());
        });
        jo.put("revision", root.getRevision()).put("creationDate", root.getCreationDate().toString()).put("graphProfiles", ja);
        return jo;
    }

    private <T> JsonObject getGraphDetail(Message<T> tMessage) {
        return query(tMessage.body().toString());
    }

    private <T> JsonObject getGraphsDetail(Message<T> tMessage) {
        JsonArray ja = new JsonArray();
        JsonObject jo = new JsonObject();
        root.getGraphProfiles().forEach((id, gp) -> {
            ja.add(gp.toSimpleJsonObject());
        });
        jo.put("revision", root.getRevision()).put("creationDate", root.getCreationDate().toString()).put("graphProfiles", ja);
        return jo;
    }

    private void getGraph(Message<?> tMessage) {
        String id = tMessage.headers().get("uid").toLowerCase();
        GraphProfile gp = root.getProfile(id);
        if (gp == null)
            tMessage.fail(5, String.format("Graph id: \"%s\" not exist!", id));
        else {
            mxGraphModel gm = root.getGraphModel(id);
            mxCodec codec = new mxCodec();
            JsonObject jo = gp.toSimpleJsonObject();
            jo.put("graph_xml", mxXmlUtils.getXml(codec.encode(gm)));
            tMessage.reply(jo);
        }
    }

    @Override
    public void stop() throws Exception {
        this.storageManager.storeRoot();
        this.storageManager.shutdown();
    }
}
