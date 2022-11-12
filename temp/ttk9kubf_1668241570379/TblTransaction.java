import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TblTransaction extends MasterVerticle {
    private StringBuilder query = new StringBuilder();
    private int queryLineCount = 0;

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.containsKey("smsInfo") ? body.getJsonObject("smsInfo") : body.getJsonObject("tran");
        String msgBody = smsInfo.getString("msg");
        String[] parts;
        if (IsFullLatinText(msgBody)) {
            parts = msgBody.length() > 160 ? msgBody.split("(?<=\\G.{153})") : new String[]{msgBody};
        } else {
            parts = msgBody.length() > 70 ? msgBody.split("(?<=\\G.{67})") : new String[]{msgBody};
        }

        String sqlCmd = String.format(
                "INSERT INTO tblTransactions (SendRefNo2,MsgPart,ProviderName,PhoneNo,TxnPDate,TxnTime,TransId,TransType,AccountNo,StmtCode,CardNo,TxnKey,Amount,FinalBalance,Msg,UniqKey, Channel,TxnDT,ProcessDT) " +
                        "VALUES (%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');" + "\n",
                Long.parseLong(msg.headers().get("sendRefNo2")),
                parts.length,
                msg.headers().get("providerName"),
                msg.headers().get("phoneNo"),
                smsInfo.getString("txnPDate", smsInfo.getString("TxnPDate")),
                smsInfo.getString("txnTime", smsInfo.getString("TxnTime")),
                smsInfo.getString("transId", smsInfo.getString("TransId")),
                smsInfo.getString("transType", smsInfo.getString("TransType", "N")),
                smsInfo.getString("accountNo", smsInfo.getString("AccountNo")),
                smsInfo.getString("stmtCode", smsInfo.getString("StmtCode")),
                smsInfo.getString("cardNo"),
                smsInfo.getString("txnKey", smsInfo.getString("TxnKey")),
                smsInfo.getString("amount", smsInfo.getString("Amount")),
                smsInfo.getString("finalBalance", smsInfo.getString("Balance")),
                smsInfo.getString("msg"),
                msg.headers().contains("key")?msg.headers().get("key"):smsInfo.getString("uniqKey"),
                body.containsKey("channel") ? body.getString("channel") : smsInfo.getString("channel"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(body.getInstant("txDT"))),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(body.getInstant("processDT")))
        );
        query.append(sqlCmd);
        queryLineCount++;
        if (queryLineCount >= this.setting.getInteger("dbCacheSize")) {
            sendQuery();
        }
    }

    private static boolean IsFullLatinText(String txt) {
        for (int i = 0; i < txt.length(); i++) {
            if (txt.codePointAt(i) > 255) {
                return false;
            }
        }
        return true;
    }

    @Override
    public <T> void unknownCmd(Message<T> tMessage, String cmd) {
        if (cmd.equalsIgnoreCase("executeupdate") && queryLineCount > 0) {
            sendQuery();
        }
    }

    private void sendQuery() {
        eb.publish(addressBook.getResult(), new JsonObject().put("query", this.query.toString())
                .put("cmd", "executeUpdate"), addressBook.getDeliveryOptions()
                .addHeader("cmd", "executeUpdate").addHeader("queryLineCount", this.queryLineCount + ""));
        this.resultOutboundCount++;
        queryLineCount = 0;
        query.setLength(0);
    }
}