import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class SetRegisteryUniqueKey extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        var key = tran.getString("CMDDATE") +
                tran.getString("CMDTIME") +
                tran.getString("ACCOUNTNO") +
                tran.getString("PHONENO") +
                tran.getString("BRANCH") +
                tran.getString("TERMINALID") +
                tran.getString("USER") +
                tran.getString("COMMAND");
        eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg).addHeader("key", key));
    }
}