package com.vx6.widget;

import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ChartVerticle extends MasterVerticle {
  private String address = null;
  private DeliveryOptions dos = null;
  private String type, kind, chartName;

  @Override
  public void initialize(Promise<Void> initPromise) throws Exception {
    this.address = "vx.mx." + config().getString("graph_id");
    JsonObject data = config().getJsonObject("data");
    this.kind = data.getJsonObject("config").getString("kind");//line, bar, pie, tool & board
    this.chartName = data.getJsonObject("config").getString("chartName");
    this.dos = new DeliveryOptions()
      .addHeader("type", config().getString("type"))
      .addHeader("id", config().getString("id"))
      .addHeader("graph_id", config().getString("graph_id"))
      .addHeader("kind", this.kind)
      .addHeader("chartName", this.chartName)
      .addHeader("address", config().getString("graph_id") + "." + config().getString("type") + "." + config().getString("id") + ".input");
    initPromise.complete();
  }

  @Override
  public <T> void trigger(Message<T> tMessage) {
    String cmd = "";
    DeliveryOptions dO = this.addressBook.getDeliveryOptions(tMessage).addHeader("address", config().getString("graph_id") + "." + config().getString("type") + "." + config().getString("id") + ".trigger");
    tMessage.headers().forEach((item) -> dO.addHeader(item.getKey(), item.getValue()));
    if (tMessage.headers().contains("cmd") && tMessage.headers().get("cmd").equalsIgnoreCase("ready")) {
      eb.publish(this.address, new JsonObject().put("signal", "ready"), dO);
    } else {
      eb.publish(this.address, tMessage.body().toString(), this.addressBook.getDeliveryOptions(tMessage).addHeader("address", config().getString("graph_id") + "." + config().getString("type") + "." + config().getString("id") + ".trigger").setHeaders(tMessage.headers()));
    }
  }

  @Override
  public <T> void process(Message<T> tMessage) {
    DeliveryOptions dO = new DeliveryOptions();
    dos.getHeaders().forEach(item -> dO.addHeader(item.getKey(), item.getValue()));
    tMessage.headers().forEach(item -> dO.addHeader(item.getKey(), item.getValue()));
    eb.publish(this.address, process(tMessage.body().toString()), dO);
  }
}
