import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class Probe extends MasterVerticle {

    @Override
    public void process(Message msg) {
        System.out.println("prob-> " + msg.body().toString());
        try {
            super.process(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}