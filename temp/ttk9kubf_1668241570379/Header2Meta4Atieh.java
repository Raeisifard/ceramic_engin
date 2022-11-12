import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Map;


public class Header2Meta4Atieh extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject metadata = new JsonObject();
        for (Map.Entry<String, String> entry : msg.headers().entries()) {
            metadata.put(entry.getKey(), entry.getValue());
        }
        ((JsonObject) msg.body()).put("metadata", metadata);
        eb.publish(addressBook.getResult(), msg.body());
    }
}