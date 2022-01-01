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
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.getJsonObject("smsInfo");
        String sqlCmd = String.format(
                "INSERT INTO tblTransactions (SendRefNo2,MsgPart,ProviderName,PhoneNo,TxnPDate,TxnTime,TransId,TransType,AccountNo,StmtCode,CardNo,TxnKey,Amount,FinalBalance,Msg,UniqKey, Channel,TxnDT,ProcessDT) " +
                        "VALUES (%d,%d,'%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s','%s');" + "\n",
                Long.parseLong(msg.headers().get("sendRefNo2")),
                Integer.parseInt(msg.headers().get("msgPart")),
                msg.headers().get("providerName"),
                msg.headers().get("phoneNo"),
                smsInfo.getString("txnPDate"),
                smsInfo.getString("txnTime"),
                smsInfo.getString("transId"),
                smsInfo.getString("transType"),
                smsInfo.getString("accountNo"),
                smsInfo.getString("stmtCode"),
                smsInfo.getString("cardNo"),
                smsInfo.getString("txnKey"),
                smsInfo.getString("amount"),
                smsInfo.getString("finalBalance"),
                smsInfo.getString("msg"),
                msg.headers().get("key"),
                msg.headers().get("channel"),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(body.getInstant("txDT"))),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(body.getInstant("processDT")))
        );
        query.append(sqlCmd);
        queryLineCount++;
        if (queryLineCount >= this.setting.getInteger("dbCacheSize")) {
            sendQuery();
        }

    }

    @Override
    public <T> void unknownCmd(Message<T> tMessage, String cmd) {
        if (cmd.equalsIgnoreCase("executeupdate") && queryLineCount > 0) {
            sendQuery();
            this.resultOutboundCount++;
        }
    }

    private void sendQuery() {
        eb.publish(addressBook.getResult(), new JsonObject().put("query", this.query.toString())
                .put("cmd", "executeUpdate"), addressBook.getDeliveryOptions()
                .addHeader("cmd", "executeUpdate").addHeader("queryLineCount", this.queryLineCount + ""));

        queryLineCount = 0;
        query.setLength(0);
    }
}