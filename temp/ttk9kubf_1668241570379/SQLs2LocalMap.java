import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

public class SQLs2LocalMap extends MasterVerticle {
    private int count = 0;
    private SharedData sharedData;
    private LocalMap<String, JsonObject> patterns;
    private static String label = "<h3 style=\"margin: 0;\">SQLs2LocalMap</h3>"+
            "<h3 style=\"display: inline-block; margin: 0;\">count:</h3>"+
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>";

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        patterns = sharedData.getLocalMap("json_sql_patterns");
        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        if (msg.body() instanceof String) {
            sendLabel(String.format(label, count == 0? "red" : "#0043ff", count), "18ff96", "50", "efff18");
            eb.publish((addressBook.getResult()), msg.body());//Patterns messages finished.
        } else {
            JsonObject pattern = (JsonObject) msg.body();
            patterns.put(pattern.getString("name"), pattern);
            count++;
            eb.publish(addressBook.getResult(), pattern, addressBook.getDeliveryOptions(msg));
        }
    }
}