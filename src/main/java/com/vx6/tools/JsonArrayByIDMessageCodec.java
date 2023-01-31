package com.vx6.tools;

import io.vertx.core.eventbus.impl.codecs.JsonArrayMessageCodec;
import io.vertx.core.json.JsonArray;

public class JsonArrayByIDMessageCodec extends JsonArrayMessageCodec {

    @Override
    public JsonArray transform(JsonArray jsonArray) {
        // If a message is sent *locally* across the event bus.
        // This example sends message just as is
        return jsonArray;
    }

    @Override
    public String name() {
        // Each codec must have a unique name.
        // This is used to identify a codec when sending a message and for unregistering codecs.
        return this.getClass().getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        // Always -1
        return -1;
    }
}
