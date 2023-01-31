package test;

import com.vx6.tools.JsonArrayByIDMessageCodec;
import com.vx6.tools.JsonObjectByIDMessageCodec;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class RegisterCustomCodec {
    private static Vertx vertx = Vertx.vertx();
    public static void main(String[] args) {
        EventBus eb = vertx.eventBus();
        JsonObjectByIDMessageCodec jsonObjectByIDMessageCodec = new JsonObjectByIDMessageCodec();
        JsonArrayByIDMessageCodec jsonArrayByIDMessageCodec = new JsonArrayByIDMessageCodec();
        eb.registerCodec(jsonObjectByIDMessageCodec);
        eb.registerCodec(jsonArrayByIDMessageCodec);
        eb.consumer("testCustomCodec", RegisterCustomCodec::printMessage);

        DeliveryOptions options = new DeliveryOptions().setCodecName(jsonObjectByIDMessageCodec.name());

        eb.publish("testCustomCodec", new JsonObject().put("name", "Test custom codec"), options);
    }

    private static void printMessage(Message<JsonObject> msg) {
        System.out.println("Message: "+ msg.body().encodePrettily());
    }
}
