import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class Pattern2LocalMap extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, JsonObject> patterns;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        patterns = sharedData.getLocalMap("json_patterns");
        super.initialize(initPromise);
    }
    @Override
    public void process(Message msg) {
        JsonObject pattern = (JsonObject) msg.body();
        patterns.put(pattern.getString("TransId"), pattern);
        eb.publish(addressBook.getResult(), pattern, addressBook.getDeliveryOptions(msg));
    }
}