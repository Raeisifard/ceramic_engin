package com.vx6.tools;

import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.eventbus.impl.codecs.JsonObjectMessageCodec;
import io.vertx.core.json.JsonObject;

public class JsonObjectByIDMessageCodec extends JsonObjectMessageCodec implements MessageCodec<JsonObject, JsonObject> {

    @Override
    public JsonObject transform(JsonObject jsonObject) {
        // If a message is sent *locally* across the event bus.
        // This example sends message just as is
        return jsonObject;
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
