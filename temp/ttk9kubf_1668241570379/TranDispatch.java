import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;


public class TranDispatch extends MasterVerticle {
    private int addAccount = 0, addCredit = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        eb.consumer("vx.mx", msg -> {
            try {
                var body = (JsonObject) msg.body();
                var time = body.getString("time").substring(14, 16) + ":" + body.getString("time").substring(17);
                //UpdateChart(time);
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
            JsonObject commands = setting.getJsonObject("COMMANDS");
            int i = 0;
            while (commands.containsKey(i + "")) {
                if (commands.getJsonObject(i + "").containsKey(command) && commands.getJsonObject(i + "").getBoolean(command)) {
                    publishOut(i, body, addressBook.getDeliveryOptions(msg));
                    return;
                } else {
                    i++;
                }
            }
            errorOutboundCount++;
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg).addHeader("error", "Unknown command."));
        } else {
            this.resultOutboundCount++;
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("error", "Has no command."));
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