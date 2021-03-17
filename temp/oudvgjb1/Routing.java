import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class Routing extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        eb.publish(addressBook.getOut("out0"), body, addressBook.getDeliveryOptions(msg));
        if (body.getString("Bill_Type").equals("UD") && body.getString("Operation_Type").equals("02")){
            eb.publish(addressBook.getOut("out1"), body, addressBook.getDeliveryOptions(msg));
        }
        try {
            super.process(body.encode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}