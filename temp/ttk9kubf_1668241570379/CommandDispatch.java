import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


public class CommandDispatch extends MasterVerticle {
    private int addAccount = 0, addCredit = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        eb.consumer("vx.mx", msg -> {
            try {
                var body = (JsonObject) msg.body();
                var time = body.getString("time").substring(14, 16) + ":" + body.getString("time").substring(17);
                UpdateChart(time);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        });
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        this.resultOutboundCount--;
        //eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg));
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        if (tran.containsKey("COMMAND")) {
            var command = tran.getString("COMMAND");
            switch (command) {
                case "ADDACCOUNT":
                    publishOut(0, body, addressBook.getDeliveryOptions(msg));
                    health.put("ADDACCOUNT", health.containsKey("ADDACCOUNT") ? health.getInteger("ADDACCOUNT") + 1 : 1);
                    addAccount++;
                    break;
                case "ADDCREDITITEM":
                    publishOut(1, body, addressBook.getDeliveryOptions(msg));
                    health.put("ADDCREDITITEM", health.containsKey("ADDCREDITITEM") ? health.getInteger("ADDCREDITITEM") + 1 : 1);
                    addCredit++;
                    break;
                default:
                    System.out.println("Command not defined.");
                    break;
            }
        } else if (tran.containsKey("Module")) {
            var module = tran.getString("Module");
            switch (body.getString("tranType")) {
                case "MQSuny":
                    publishOut(2, body, addressBook.getDeliveryOptions(msg));
                    break;
                case "FileSuny":
                    publishOut(3, body, addressBook.getDeliveryOptions(msg));
                    break;
            }
        }
    }

    private void UpdateChart(String time) {
        JsonObject data = new JsonObject();
        data.put("labels", new JsonArray().add(time));
        data.put("datasets", new JsonArray()
                .add(new JsonObject().put("data", new JsonArray().add(addAccount)))
                .add(new JsonObject().put("data", new JsonArray().add(addCredit))));
        sendChart(new JsonObject().put("data", data));
    }
}