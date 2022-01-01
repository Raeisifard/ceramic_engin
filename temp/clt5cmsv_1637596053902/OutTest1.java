import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Random;

public class OutTest1 extends MasterVerticle {
    private static Random rand = new Random();

    @Override
    public void process(Message msg) {
        //JsonObject body = (JsonObject) msg.body();
        System.out.println("out1#" + ": " + msg.body().toString());
        try {
            super.process(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}