import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Random;

public class SampleVerticle extends MasterVerticle {
    private static Random rand = new Random();

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        body.put("data", rand.nextInt(101));
    
       eb.publish(addressBook.getOut("out0"), body, addressBook.getDeliveryOptions(msg));
    }
}