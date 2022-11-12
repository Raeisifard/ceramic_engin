import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class RejectOldSuny extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        var module = tran.getString("Module");
        var txnKey = tran.getString("TxnKey");
        var txnPDate = tran.getString("TxnPDate").trim();
        if (txnPDate.equals("999999") || txnPDate.equals("000000") || txnPDate.equals("0") || txnPDate.equals("")
                || txnPDate.equals("00000000000000000000") || txnPDate.equals(txnKey.substring(0, 6)) || module.equals("IN45")) {
            txnPDate = txnKey.substring(0, 6);
        } else {
            txnPDate = txnPDate.substring(0, 6);
        }
        txnPDate = Library.purePDateStr(txnPDate);
        if (setting.getBoolean("enable")) {
            if (txnPDate.compareTo(PersianCalendarBean.getPDateStr(-1)) <= -1) {
                eb.publish(addressBook.getError(), msg.body(), addressBook.getDeliveryOptions(msg)
                        .addHeader("status", "error").addHeader("error", "old trans"));
                resultOutboundCount--;
                errorOutboundCount++;
                return;
            }
        }
        tran.put("TxnPDate", txnPDate);
        var txnTime = tran.getString("TxnTime").trim();
        if (txnTime.equals("999999") || txnTime.equals("000000") || txnTime.equals("0") || txnTime.equals("") || module.equals("IN45"))
            txnTime = txnKey.substring(6, 12);
        else
            txnTime = txnTime.substring(0, 6);
        tran.put("TxnTime", txnTime);
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    }
}