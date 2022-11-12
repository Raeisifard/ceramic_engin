import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

public class MQSuny2Json extends MasterVerticle {

    @Override
    public <T> void process(Message<T> msg) {
        resultOutboundCount--;
        if (msg.body() instanceof String) {
            String rawSuny = (String) msg.body();
            String inLine = Library.replaceChar(rawSuny, 124, 146, '|', ';');
            inLine = Library.replaceChar(inLine, 147, 178, '|', ';');
            inLine = Library.replaceChar(inLine, 179, 299, '|', ';');
            String[] lineSplit = inLine.split("\\|", -1);
            if (lineSplit.length < 30)
                sendError(msg);
            else {
                JsonObject suny = new JsonObject();
                suny.put("AccountNo", lineSplit[0]);
                suny.put("TxnKey", lineSplit[1]);
                suny.put("Date", lineSplit[2]);
                suny.put("Module", lineSplit[3].toUpperCase());
                suny.put("InputSource", lineSplit[4]);
                suny.put("Amount", lineSplit[5]);
                suny.put("BranchCode", lineSplit[6]);
                suny.put("StmtCode", lineSplit[7]);
                suny.put("UserID", lineSplit[8]);
                suny.put("TransIndicator", lineSplit[9]);
                suny.put("OverrideUserID", lineSplit[10]);
                suny.put("ModeFlag", lineSplit[11]);
                suny.put("BookIndicator", lineSplit[12]);
                suny.put("TxnPDate", lineSplit[13].trim().length() == 6 ? lineSplit[13] : lineSplit[1].substring(0, 6));
                suny.put("TxnTime", lineSplit[14].trim().length() == 6 ? lineSplit[14] : lineSplit[1].substring(6, 12));
                suny.put("TerminalID", lineSplit[15]);
                suny.put("SeqNo", lineSplit[16]);
                suny.put("VariableData", lineSplit[17]);
                suny.put("OptionalInfo", lineSplit[18]);
                suny.put("Comment3", lineSplit[19]);
                suny.put("Balance", lineSplit[20]);
                suny.put("EqualRialAmount", lineSplit[21]);
                suny.put("CARD_PAN1", lineSplit[22]);
                suny.put("CARD_PAN2", lineSplit[23]);
                suny.put("CARD_ACNO1", lineSplit[24]);
                suny.put("CARD_ACNO2", lineSplit[25]);
                suny.put("PRCODE", lineSplit[26]);
                suny.put("ACQ_BIN", lineSplit[27]);
                suny.put("ISS_BIN ", lineSplit[28]);
                suny.put("OPERCODE", lineSplit[29]);
                suny.put("rawSuny", rawSuny);
                eb.publish(addressBook.getResult(), new JsonObject().put("tran", suny)
                        .put("rawData", rawSuny).put("channel", "MQ|Suny").put("tranType", "MQSuny"));
                resultOutboundCount++;
            }
        } else {
            sendError(msg);
        }
    }

    private <T> void sendError(Message<T> msg){
        eb.publish(addressBook.getError(), msg.body(), addressBook.getDeliveryOptions(msg)
                .addHeader("status", "error")
                .addHeader("error", "Suny format is not compatible."));
        errorOutboundCount++;
    }
}