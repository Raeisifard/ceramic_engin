package com.vx6.utils;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GraphProcess {
  private JsonArray jsonArrayGraph;

  public GraphProcess() {
    super();
  }

  public GraphProcess(mxGraphModel graphModel) {
    //TODO get jsonArrayGraph from mxGraphModel.
    this.jsonArrayGraph = makeJsonArrayGraph(graphModel);
  }

  private static JsonArray makeJsonArrayGraph(mxGraphModel graphModel) {
    JsonArray ja = new JsonArray();
    graphModel.getCells().forEach((k, c) -> {
      mxCell cell = (mxCell) c;
      if (cell.isVertex() && StringUtils.isNotEmpty(cell.getType()) && !cell.getType().equalsIgnoreCase("info")) {
        JsonObject jo = new JsonObject();
        jo.put("id", cell.getId());
        jo.put("type", cell.getType());
        jo.put("data", cell.getData());
        if (cell.isConnectable()) {
          getPorts(cell, jo);
        } else
          for (int i = 0; i < cell.getChildCount(); i++) {
            mxCell pCell = (mxCell) cell.getChildAt(i);
            if (pCell.isConnectable()) {
              getPorts(pCell, jo);
            }
          }
        ja.add(jo);
      }
    });
    return ja;
  }

  private static void getPorts(mxCell cell, JsonObject jo) {
    List<String> edges = new ArrayList<>();
    if (cell.getEdgeCount() == 0) {
      jo.put(cell.getValue().toString(), edges);
    } else {
      if (cell.getDirection().equalsIgnoreCase("in")) {
        for (int i = 0; i < cell.getEdgeCount(); i++) {
          mxCell sourceCell = (mxCell) ((mxCell) cell.getEdgeAt(i)).getSource();
          if (!Objects.equals(sourceCell.getId(), cell.getId())) {
            String type = StringUtils.isNotEmpty(sourceCell.getType()) ? sourceCell.getType() : ((mxCell) sourceCell.getParent()).getType();
            String id = StringUtils.isNotEmpty(sourceCell.getType()) ? sourceCell.getId() : ((mxCell) sourceCell.getParent()).getId();
            String value = sourceCell.getValue().toString();
            edges.add(String.join(".", type, id, value).replaceAll(" ", "_").toLowerCase());
          }
        }
        jo.put(cell.getValue().toString(), edges);
      } else if (cell.getDirection().equalsIgnoreCase("out")) {
        for (int i = 0; i < cell.getEdgeCount(); i++) {
          mxCell targetCell = (mxCell) ((mxCell) cell.getEdgeAt(i)).getTarget();
          if (!Objects.equals(targetCell.getId(), cell.getId())) {
            String type = StringUtils.isNotEmpty(targetCell.getType()) ? targetCell.getType() : ((mxCell) targetCell.getParent()).getType();
            String id = StringUtils.isNotEmpty(targetCell.getType()) ? targetCell.getId() : ((mxCell) targetCell.getParent()).getId();
            String value = targetCell.getValue().toString();
            edges.add(String.join(".", type, id, value).replaceAll(" ", "_").toLowerCase());
          }
        }
        jo.put(cell.getValue().toString(), edges);
      }else if (cell.getDirection().equalsIgnoreCase("inout")) {
        for (int i = 0; i < cell.getEdgeCount(); i++) {
          mxCell targetCell = (mxCell) ((mxCell) cell.getEdgeAt(i)).getTarget();
          if (!Objects.equals(targetCell.getId(), cell.getId())) {
            String type = StringUtils.isNotEmpty(targetCell.getType()) ? targetCell.getType() : ((mxCell) targetCell.getParent()).getType();
            String id = StringUtils.isNotEmpty(targetCell.getType()) ? targetCell.getId() : ((mxCell) targetCell.getParent()).getId();
            String value = targetCell.getValue().toString();
            edges.add(String.join(".", type, id, value).replaceAll(" ", "_").toLowerCase());
          }
        }
        jo.put(cell.getValue().toString(), edges);
      }
    }
  }

  public JsonArray getJsonArrayGraph() {
    return jsonArrayGraph;
  }

  public void setJsonArrayGraph(JsonArray jsonArrayGraph) {
    this.jsonArrayGraph = jsonArrayGraph;
  }


}
