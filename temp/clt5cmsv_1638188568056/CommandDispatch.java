import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class CommandDispatch extends MasterVerticle {


    @Override
    public void process(Message msg) {
        this.resultOutboundCount--;
        //eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg));
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        var command = tran.getString("COMMAND");
        switch (command) {
            case "ADDACCOUNT":
                publishOut(0, body, addressBook.getDeliveryOptions(msg));
                health.put("ADDACCOUNT", health.containsKey("ADDACCOUNT") ? health.getInteger("ADDACCOUNT") + 1 : 1);
                break;
            case "ADDCREDITITEM":
                publishOut(1, body, addressBook.getDeliveryOptions(msg));
                health.put("ADDCREDITITEM", health.containsKey("ADDCREDITITEM") ? health.getInteger("ADDCREDITITEM") + 1 : 1);
                break;
            default:
                System.out.println("Command not defined.");
                break;
        }
    }
}