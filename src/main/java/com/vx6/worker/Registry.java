package com.vx6.worker;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxXmlUtils;
import com.vx6.tools.DataRoot;
import com.vx6.tools.GraphProfile;
import io.vertx.core.*;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import one.microstream.storage.configuration.Configuration;
import one.microstream.storage.types.EmbeddedStorageManager;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.UUID;

public class Registry extends AbstractVerticle {
    private final DataRoot root = new DataRoot();
    //private final JsonObject root = new JsonObject();
    private EmbeddedStorageManager storageManager;
    private static final String data_dir = "data";
    private static final String backup_dir = "data_backup";
    private LocalMap<String, JsonObject> graphMap;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        graphMap = sharedData.getLocalMap("_FORM_GRAPH_MAP");
        vertx.eventBus().consumer("registry", this::processMessage);
        // configuring the database via .ini file instead of API. Here the directory and the thread count.
        final Configuration configuration = Configuration.LoadIni(
                Registry.class.getResource("/microstream/storage.ini"));
        this.storageManager = configuration.createEmbeddedStorageFoundation().createEmbeddedStorageManager(root).start();
        if (storageManager.root() == null) {
            storageManager.storeRoot();
        }
        if (config().getBoolean("auto_deploy")) {
            root.getGraphProfiles().forEach((graph_id, graphProfile) -> {
                if (graphProfile.isActive()) {
                    graphProfile.setActive(false);
                    String uuid = UUID.randomUUID().toString();
                    JsonObject graph = graphProfile.toSimpleJsonObject().put("graph", graphProfile.getGraph_xml());
                    graphMap.put(uuid, graph.put("time", new Date().toInstant()).put("zip", false));
                    vertx.eventBus().send("registry.vx", graphProfile.toSimpleJsonObject().put("graph", uuid), new DeliveryOptions()
                            .setHeaders(graphProfile.toSimpleMultiMap()).addHeader("cmd", "load"));
                }
            });
        }
        startPromise.complete();
    }

    private void processMessage(Message<Object> tMessage) {
        String cmd = tMessage.headers().contains("cmd") ? tMessage.headers().get("cmd") : ((JsonObject) tMessage.body()).getString("cmd");
        GraphProfile gp;
        JsonObject body;
        switch (cmd.toLowerCase()) {
            case "query"://Simple graph profile
                JsonObject jo = query(tMessage.body().toString());
                if (jo != null)
                    tMessage.reply(jo);
                else
                    tMessage.fail(5, String.format("Graph id: \"%s\" not exist!", tMessage.body().toString()));
                break;
            case "add_or_update":
                body = (JsonObject) tMessage.body();
                gp = new GraphProfile(body);
                if (body.containsKey("graph")) {
                    JsonObject graph = graphMap.get(body.getString("graph"));
                    gp.put("graph_xml", graph.getString("graph"));
                    gp.addRevision();
                } else {
                    gp.addModification();
                    tMessage.reply(root.getProfile(gp.getGraph_id()).toSimpleJsonObject());
                }
                root.addOrReplace(gp);
                storageManager.store(root.getGraphProfiles());
                storageManager.store(root.graphModels);
                break;
            case "remove":
                removeGraphId(tMessage);
                break;
            case "save":
                body = (JsonObject) tMessage.body();
                String graph_id = body.getString("uid");
                String graph_name = body.getString("name");
                JsonObject graph = graphMap.get(body.getString("graph"));
                String graphXml =graph.getString("graph");
                Document document = mxXmlUtils.parseXml(graphXml);
                if (document != null) {
                    GraphProfile graphProfile = root.getProfile(graph_id);
                    if (graphProfile != null) {
                        graphProfile.setGraph_xml(graphXml);
                        graphProfile.addModification();
                    } else {
                        graphProfile = new GraphProfile(body.copy().put("graph_xml", graphXml)
                                .put("graph_id", graph_id)
                                .put("graph_name", graph_name));
                        graphProfile.addRevision();
                        graphProfile.setDeploy_id("NEVER_BEEN_DEPLOYED_YET");
                        graphProfile.setActive(false);
                    }
                    root.addOrReplace(graphProfile);
                    storageManager.store(root.getGraphProfiles());
                    storageManager.store(root.graphModels);
                    tMessage.reply(body);
                } else {
                    tMessage.fail(10, String.format("Graph \"%s\" has no valid xml.", graph_name));
                }
                break;
            case "set":
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
            case "graphs_detail":
                tMessage.reply(getGraphDetail(tMessage));
                break;
            case "get_graph":
            case "view":
                getView(tMessage);
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
            GraphProfile gp = new GraphProfile(root.getGraphProfiles().get(graph_id).copy());
            gp.setGraph_name(graph_name);
            root.getGraphProfiles().replace(graph_id, gp);
            mxCell cell = (mxCell) root.getGraphModel(graph_id).getCell("0");
            cell.setName(graph_name);
            storageManager.store(root.getGraphProfiles());
            storageManager.store(cell);
            tMessage.reply(String.format("Changing name of graph_id: %s to %s was successful!", graph_id, graph_name));
        } catch (Exception e) {
            tMessage.fail(1, String.format("Chould not change name of graph_id: %s to %s!", graph_id, graph_name));
            e.printStackTrace();
        }
    }

    private JsonObject query(String graph_id) {
        GraphProfile gp = root.getProfile(graph_id);
        String uuid = UUID.randomUUID().toString();
        if (gp != null) {
            graphMap.put(uuid, gp.put("time", new Date().toInstant()));
            return gp.toSimpleJsonObject().put("graph", uuid);
        } else
            return null;
    }

    private <T> void set2MxGraphModel(Message<T> tMessage) {
        mxGraphModel gm = root.getGraphModel(tMessage.headers().get("uid").toLowerCase());
        mxCell cell = (mxCell) gm.getCell(tMessage.headers().get("id"));
        if (cell == null)
            return;
        JsonObject jo = (JsonObject) cell.getData();
        jo.put("setting", new JsonObject(tMessage.body().toString()));
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

    private void getGraph(Message tMessage) {
        String id = tMessage.headers().get("uid").toLowerCase();
        GraphProfile gp = root.getProfile(id);
        if (gp == null)
            tMessage.fail(5, String.format("Graph id: \"%s\" not exist!", id));
        else {
            mxGraphModel gm = root.getGraphModel(id);
            mxCodec codec = new mxCodec();
            JsonObject jo = gp.toSimpleJsonObject();
            UUID uuid = UUID.randomUUID();
            graphMap.put(uuid.toString(), jo.copy().put("graph", mxXmlUtils.getXml(codec.encode(gm))));
            tMessage.reply(jo.put("graph", uuid.toString()));
        }
    }

    private void getView(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        String id = body.getString("uid");
        GraphProfile gp = root.getProfile(id);
        if (gp != null) {
            UUID uuid = UUID.randomUUID();
            graphMap.put(uuid.toString(), new JsonObject()
                    .put("graph", gp.getGraph_xml())
                    .put("uid", gp.getGraph_id())
                    .put("graphName", gp.getGraph_name())
                    .put("active", gp.isActive())
                    .put("time", new Date().toInstant()));
            msg.reply(new JsonObject().put("graph", uuid.toString()).put("uid", gp.getGraph_id())
                    .put("graphName", gp.getGraph_name())
                    .put("active", gp.isActive()));
        } else {
            msg.fail(3, "NOT_EXIST");
        }
    }

    @Override
    public void stop() throws Exception {
        this.storageManager.storeRoot();
        this.storageManager.shutdown();
    }
}
