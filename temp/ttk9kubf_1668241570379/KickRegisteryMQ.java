import com.vx6.master.MasterVerticle;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class KickRegisteryMQ extends MasterVerticle {
    private boolean initiated = false;
    private JsonObject cBody = new JsonObject();
    private MultiMap cHeaders;
    private static final String green = "<div style=\" width: 25px; height: 25px; margin: 10px auto; background-color: #ABFF00; border-radius: 50%; box-shadow: rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #441313 0 -1px 9px, rgba(255, 0, 0, 0.5) 0 2px 12px;\"></div>";
    private static final String red = "<div style=\" width: 25px; height: 25px; margin: 10px auto; background-color: #F00; border-radius: 50%; box-shadow: rgba(0, 0, 0, 0.2) 0 -1px 7px 1px, inset #304701 0 -1px 9px, #89FF00 0 2px 12px;\"></div>";

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sendLabel(initiated ? green : red);
        initPromise.complete();
    }

    @Override
    public <T> void status(Message<T> msg) {
        if (msg.headers().contains("name") && msg.headers().get("name").equals("init")) {
            initiated = true;
            sendLabel(green);
            if (!(cBody.isEmpty() && (cHeaders == null || cHeaders.isEmpty()))) {
                eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg));
                cBody.clear();
                cHeaders.clear();
                this.resultOutboundCount++;
            }
        }
    }

    @Override
    public <T> void noCmd(Message<T> msg, String cmd) {
        try {
            JsonObject body = (JsonObject) msg.body();
            if (initiated) {
                eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg));
                this.resultOutboundCount++;
            } else {
                cBody = body;
                cHeaders = msg.headers();
            }
        } catch (Exception ignored) {
        }
    }
}