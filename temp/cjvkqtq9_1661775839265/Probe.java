import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

public class Probe extends MasterVerticle {
    private final String className = this.getClass().getName();
    private int process = 0, trigger = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        eb.consumer("vx.mx", msg -> {
            try {
                var body = (JsonObject) msg.body();
                if (!Objects.equals(body.getString("type"), "sync"))
                    return;
                var time = body.getString("time").substring(14, 16) + ":" + body.getString("time").substring(17);
                UpdateChart(time);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        });
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        process++;
        System.out.println(className + "(" + config().getString("id") + ").Input.body: " + msg.body());
        System.out.println(className + "(" + config().getString("id") + ").Input.header: " + msg.headers());
    }

    @Override
    public void trigger(Message msg) {
        trigger++;
        System.out.println(className + "(" + config().getString("id") + ").Trigger.body: " + msg.body());
        System.out.println(className + "(" + config().getString("id") + ").Trigger.header: " + msg.headers());
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        /*holdOn = false;
        if (autoNext) {
            eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
        }*/
    }

    private void UpdateChart(String time) {
        JsonObject data = new JsonObject();
        data.put("labels", new JsonArray().add(time));
        data.put("datasets", new JsonArray()
                .add(new JsonObject().put("data", new JsonArray().add(process)))
                .add(new JsonObject().put("data", new JsonArray().add(trigger))));
        sendChart(new JsonObject().put("data", data));
    }
}