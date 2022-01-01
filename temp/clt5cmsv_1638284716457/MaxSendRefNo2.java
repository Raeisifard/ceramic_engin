import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class MaxSendRefNo2 extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg).addHeader("cmd", "MaxSendRefNo2"));//.addHeader("buffer_size", buffer.getBuffSize() + ""));
    }
}