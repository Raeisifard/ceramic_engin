package com.vx6.widget;

import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class ParquetVerticle extends MasterVerticle {
private JsonObject data;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        data = config().getJsonObject("data");
        initPromise.complete();
    }

    @Override
    public <T> void unknownCmd(Message<T> tMessage, String cmd) {
        eb.publish(addressBook.getResult(), data.getString("code"), addressBook.getDeliveryOptions(tMessage).addHeader("type", "parquet"));
        this.resultOutboundCount++;
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        stopPromise.complete();
    }
}
