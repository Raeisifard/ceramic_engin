import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class SetTranUniqueKey extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        String txnKey = tran.getString("UNIQUE_ID", tran.getString("UNIQ_ID", tran.getString("UNIQID", tran.getString("ORGNLTXID", (tran.containsKey("TERMID") && tran.containsKey("SEQNUM")) ? tran.getString("TxnPDate") + tran.getString("TxnTime") + tran.getString("TERMID") + tran.getString("SEQNUM") : ""))));
        tran.put("TxnKey", txnKey);
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg).addHeader("key", txnKey));
    }
}