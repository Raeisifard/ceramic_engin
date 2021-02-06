package com.vx6.tools;

import com.mxgraph.io.mxCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxXmlUtils;
import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DataRoot {
    private static final String Version = "0.0.2";
    private long revision = 0;
    private Date creationDate = new Date();
    private Map<String, GraphProfile> graphProfiles = new HashMap<>();
    public Map<String, mxGraphModel> graphModels = new HashMap<>();

    public DataRoot() {
        super();
    }

    public void addProfile(GraphProfile profile) throws Exception {//Deploy Graph
        String graph_id = profile.getGraph_id();
        if (graphProfiles.containsKey(graph_id))
            throw new Exception("Already contains this graph_id: " + graph_id);
        Document document = mxXmlUtils.parseXml(profile.getGraph_xml());
        mxCodec codec = new mxCodec(document);
        assert document != null;
        mxGraphModel graphModel = (mxGraphModel) codec.decode(document.getDocumentElement());
        this.graphProfiles.put(graph_id, profile);
        this.graphModels.put(graph_id, graphModel);
        revision++;
    }

    public GraphProfile removeProfile(String graph_id) throws Exception {//Undeploy Graph
        if (graphProfiles.containsKey(graph_id)) {
            revision++;
            graphModels.remove(graph_id);
            return graphProfiles.remove(graph_id);
        } else
            throw new Exception("There is no such graph_id: " + graph_id);
    }

    public GraphProfile replaceProfile(String graph_id, GraphProfile profile) throws Exception {//Redeploy Graph
        if (graphProfiles.containsKey(graph_id)) {
            revision++;
            Document document = mxXmlUtils.parseXml(profile.getGraph_xml());
            mxCodec codec = new mxCodec(document);
            assert document != null;
            mxGraphModel graphModel = (mxGraphModel) codec.decode(document.getDocumentElement());
            this.graphModels.replace(graph_id, graphModel);
            return this.graphProfiles.replace(graph_id, profile);
        } else
            throw new Exception("There is no such graph_id to replace: " + graph_id);
    }

    public void addOrReplace(GraphProfile gp) {
        String graph_id = gp.getGraph_id();
        GraphProfile graphProfile = graphProfiles.getOrDefault(graph_id, null);
        if (graphProfile == null) {
            Document document = mxXmlUtils.parseXml(gp.getGraph_xml());
            mxCodec codec = new mxCodec(document);
            assert document != null;
            mxGraphModel graphModel = (mxGraphModel) codec.decode(document.getDocumentElement());
            this.graphModels.put(graph_id, graphModel);
            this.graphProfiles.put(graph_id, gp);
        } else if (gp.getGraph_xml() == null) {
            String graph_xml = graphProfile.getGraph_xml();
            gp.put("graph_xml", graph_xml);
            this.graphProfiles.put(graph_id, gp);
        } else {
            Document document = mxXmlUtils.parseXml(gp.getGraph_xml());
            mxCodec codec = new mxCodec(document);
            assert document != null;
            mxGraphModel graphModel = (mxGraphModel) codec.decode(document.getDocumentElement());
            this.graphProfiles.put(graph_id, gp);
            this.graphModels.put(graph_id, graphModel);
        }
    }

    public GraphProfile getProfile(String graph_id) {
        return this.graphProfiles.getOrDefault(graph_id, null);
    }

    public mxGraphModel getGraphModel(String graph_id) {
        return this.graphModels.getOrDefault(graph_id, null);
    }

    public void setGraphModel(mxGraphModel graphModel) {
        String graph_id = ((mxCell) graphModel.getCell("0")).getUid();
        this.graphModels.put(graph_id, graphModel);
    }

    public Map<String, GraphProfile> getGraphProfiles() {
        return this.graphProfiles;
    }

    public String getVersion() {
        return Version;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public long getRevision() {
        return revision;
    }

    public void setGraphName(String graph_id, String graph_name) throws Exception {
        graphProfiles.get(graph_id).setGraph_name(graph_name);
    }

    @Override
    public String toString() {
        return "Revision: (" + revision + ") graph: " + this.graphProfiles.size() + " : " + this.graphProfiles.keySet().toString();
    }
}
