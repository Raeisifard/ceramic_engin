import com.vx6.master.MasterVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.util.HashMap;


public class Initialization extends MasterVerticle {
    private boolean initiated = false;
    private HashMap<Integer, JsonObject> bodies = new HashMap<>();
    private HashMap<Integer, MultiMap> headers = new HashMap();
    private SharedData sharedData;
    private LocalMap<String, JsonObject> constants;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        constants = sharedData.getLocalMap("SAPTA_CONSTANTS");
        constants.put("AYN", config);
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        try {
            JsonObject body = (JsonObject) msg.body();
            if (body.getString("msg").equalsIgnoreCase("result-set-end")) {
                initiated = true;
                //Ready to turn on inputs
                for (Integer i : bodies.keySet()) {
                    if (bodies.get(i) != null)
                        publishOut(i, bodies.get(i), addressBook.getDeliveryOptions().setHeaders(headers.get(i)));
                }
                bodies.clear();
                headers.clear();
            }
        } catch (Exception ignored) {
        }
        System.out.println(msg.body());
    }

    @Override
    public <T> void trigger(Message<T> msg) {
        try {
            JsonObject body = (JsonObject) msg.body();
            if (initiated) {
                publishOut(body.getInteger("no"), msg.body(), addressBook.getDeliveryOptions(msg));
                this.resultOutboundCount++;
            } else {
                bodies.put(body.getInteger("no"), body);
                headers.put(body.getInteger("no"), msg.headers());
            }
        } catch (Exception ignored) {
        }
    }
}