import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class SplitTrans extends MasterVerticle {

    @Override
    public void process(Message msg) {
        JsonObject joTran = new JsonObject();
        String transRaw = msg.body().toString();
        if (transRaw.trim().length() == 0)
            return;
        String[] transSplit = transRaw.split("\\|");
        System.out.println(transRaw);
        joTran.put("AccountNumber", transSplit[0])
        .put("TxnKey", transSplit[1])
        .put("Date", transSplit[2])
        .put("module", transSplit[3])
        .put("inputSource", transSplit[4])
        .put("amount", transSplit[5])
        .put("branchCode", transSplit[6])
        .put("stmtCode", transSplit[7])
        .put("userID", transSplit[8].trim())
        .put("tranIndicator", transSplit[9])
        .put("overrideUserID", transSplit[10])
        .put("modeFlag", transSplit[11])
        .put("bookIndicator", transSplit[12])
        .put("txnPDate", transSplit[13].substring(0, 2) + transSplit[13].substring(2, 4) + transSplit[13].substring(4, 6))
        .put("txnTime", transSplit[14].substring(0, 2) + transSplit[14].substring(2, 4) + transSplit[14].substring(4, 6))
        .put("terminalID", transSplit[15])
        .put("seqNo", transSplit[16])
        .put("variableData", transSplit[17])
        .put("optionalInfo", transSplit[18])
        .put("comment3", transSplit[19]);
        
        if (transSplit[3].equals("ACH0") && transSplit[7].equals("399")) {
            joTran.put("chequeNumber", transSplit[17].trim());
            }
        eb.publish(addressBook.getResult(), joTran, addressBook.getDeliveryOptions(msg));
    }
}