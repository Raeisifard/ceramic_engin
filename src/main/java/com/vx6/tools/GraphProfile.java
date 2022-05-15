package com.vx6.tools;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GraphProfile extends JsonObject {
    public GraphProfile() {
        super();
    }

    public GraphProfile(String graph_id) {
        this();
        this.put("graph_id", graph_id.toLowerCase());
        //this.addRevision();
    }

    public GraphProfile(JsonObject jo) {
        this(jo.getString("graph_id").toLowerCase());
        if (jo.containsKey("graph_name")) this.setGraph_name(jo.getString("graph_name"));
        if (jo.containsKey("deploy_id")) this.setDeploy_id(jo.getString("deploy_id"));
        if (jo.containsKey("active")) this.setActive(jo.getBoolean("active"));
        if (jo.containsKey("revision")) this.setRevision(jo.getLong("revision"));
        if (jo.containsKey("modifications")) this.setModifications(jo.getJsonArray("modifications"));
        if (jo.containsKey("graph_xml")) this.setGraph_xml(jo.getString("graph_xml"));
    }

    public Long addRevision() {
        if (this.containsKey("revision"))
            this.setRevision(this.getRevision() + 1);
        else
            this.setRevision(1L);
        this.addModification();
        return this.getRevision();
    }

    public JsonArray addModification() {
        if (this.containsKey("modifications"))
            this.put("modifications", this.getModifications().add(new Date().toString()));
        else
            this.put("modifications", new JsonArray().add(new Date().toString()));
        return this.getModifications();
    }

    public String getGraph_name() {
        return this.getString("graph_name");
    }

    public GraphProfile setGraph_name(String graph_name) {
        this.put("graph_name", graph_name);
        return this;
    }

    public String getDeploy_id() {
        return this.getString("deploy_id");
    }

    public void setDeploy_id(String deploy_id) {
        this.put("deploy_id", deploy_id);
    }

    public String getGraph_id() {
        return this.getString("graph_id").toLowerCase();
    }

    public void setGraph_id(String graph_id) {
        this.put("graph_id", graph_id.toLowerCase());
    }

    public boolean isActive() {
        return this.containsKey("active") ? this.getBoolean("active") : false;
    }

    public void setActive(boolean active) {
        this.put("active", active);
    }

    public Long getRevision() {
        return this.getLong("revision");
    }

    public void setRevision(Long revision) {
        this.put("revision", revision);
    }

    public JsonArray getModifications() {
        return this.getJsonArray("modifications");
    }

    public void setModifications(JsonArray modifications) {
        this.put("modifications", modifications);
    }

    private void setGraph_xml(String graph_xml) {
        this.put("graph_xml", graph_xml);
    }

    public String getGraph_xml() {
        return this.getString("graph_xml");
    }

    public JsonObject toSimpleJsonObject() {
        JsonObject cjo = this.copy();
        //cjo.put("graph_name", getGraph_name());
        cjo.remove("graph_xml");
        return cjo;
    }

    public MultiMap toMultiMap() {
        MultiMap mm = MultiMap.caseInsensitiveMultiMap();
        for (String key : this.getMap().keySet()) {
            mm.add(key, this.getMap().get(key) != null ? this.getMap().get(key).toString() : "THERE_IS_NO_DEPLOY_ID");
        }
        List mods = new ArrayList();
        mods = this.getModifications().getList();
        mm.set("modifications", (Iterable<String>) mods);
        return mm;
    }

    public MultiMap toSimpleMultiMap() {
        MultiMap mm = MultiMap.caseInsensitiveMultiMap();
        mm.add("graph_id", this.getGraph_id())
                .add("graph_name", this.getGraph_name())
                .add("revision", this.getRevision() + "")
                .add("active", this.isActive() + "");
        return mm;
    }
}
