import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class RejectOldTran extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        var txnPDate = tran.getString("DATE", tran.getString("STMP_DATE", tran.containsKey("ORGNLENDTOENDID") ? tran.getString("ORGNLENDTOENDID").substring(0, 6) : PersianCalendarBean.getPDateStr(0))).trim();
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
        var txnTime = tran.getString("TIME", tran.getString("STMP_TIME", "000000")).trim();
        tran.put("TxnTime", txnTime);
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    }
}