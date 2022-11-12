import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class MaxSendRefNo2 extends MasterVerticle {
    private static String label = "<h3 style=\"margin: 0;\">MaxSendRefNo2</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>";

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        var maxSendRefNo2 = Long.parseLong(body.getString("MaxSendRefNo2"));
        sendLabel(String.format(label, maxSendRefNo2 == 0 ? "red" : "#0043ff", maxSendRefNo2), "18ff96", "50", "efff18");
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg).addHeader("cmd", "status").addHeader("name", "MaxSendRefNo2"));//.addHeader("buffer_size", buffer.getBuffSize() + ""));
    }
}