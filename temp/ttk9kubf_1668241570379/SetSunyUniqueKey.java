import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class SetSunyUniqueKey extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        String txnKey = tran.getString("TxnKey");
        String key;
        if (txnKey.substring(0, 2).trim().length() == 2 && Integer.parseInt(txnKey.substring(0, 2)) >= 90)
            key = "13" + txnKey + tran.getString("AccountNo"); //+ module + tranIndicator;
        else
            key = "14" + txnKey + tran.getString("AccountNo");// + module + tranIndicator;
        if (tran.getString("Module").equalsIgnoreCase("IN45"))
            key = key + tran.getString("OptionalInfo").trim();  // comment2
        eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg).addHeader("key", key));
    }
}