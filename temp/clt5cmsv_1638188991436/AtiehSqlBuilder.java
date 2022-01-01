import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


public class AtiehSqlBuilder extends MasterVerticle {
    private String providerName;
    private String srcPhoneNo;
    Random rd = new Random();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        this.providerName = config().getJsonObject("data").getJsonObject("config").getString("name");
        this.srcPhoneNo = correctAtiehPhoneNo(config().getJsonObject("data").getJsonObject("config").getString("phoneNo"));
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.getJsonObject("smsInfo");
        String msgBody = smsInfo.getString("msg");
        String[] parts;
        int dcs;
        if (IsFullLatinText(msgBody)) {
            parts = msgBody.length() > 160 ? msgBody.split("(?<=\\G.{153})") : new String[]{msgBody};
            dcs = 0;
        } else {
            parts = msgBody.length() > 70 ? msgBody.split("(?<=\\G.{67})") : new String[]{msgBody};
            dcs = 8;
        }
        StringBuilder sqlStringBuilder = new StringBuilder();
        byte[] randByte = new byte[1];
        rd.nextBytes(randByte);
        for (int i = 0; i < parts.length; i++) {
            String udh = parts.length == 1 ? "" : String.format("050003%02x%02d%02d", randByte[0], parts.length, i + 1);
            sqlStringBuilder.append(String.format(
                    "INSERT INTO outbound_messages (creation_date, from_mobile_number, dest_mobile_number, message_body, due_date, DCS, UDH, Info,Info2,Info3,TXDateTime, Reported,ReportedSla, SendRefNo2,BankId)" +
                            " VALUES ('%s','%s','%s','%s','%s',%d,'%s','%s','%s','%s','%s',%d,%d,%d,'%s');\n",
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    srcPhoneNo,
                    correctAtiehPhoneNo(msg.headers().get("phoneNo")),
                    parts[i].replace("^", "\n"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                    dcs,
                    udh,
                    smsInfo.getString("accountNo"),
                    smsInfo.getString("transId"),
                    smsInfo.getString("transGroup"),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(body.getInstant("txDT"))),
                    0,
                    0,
                    Long.parseLong(msg.headers().get("sendRefNo2")),
                    smsInfo.getString("bankId")
            ));
        }
        eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg)
                .addHeader("MsgPart", parts.length + "")
                .addHeader("providerName", providerName));
        this.errorOutboundCount++;
        eb.publish(addressBook.getResult(), body.put("sql",
                sqlStringBuilder.toString()),
                addressBook.getDeliveryOptions(msg).addHeader("providerName", providerName));
    }

    private static boolean IsFullLatinText(String txt) {
        for (int i = 0; i < txt.length(); i++) {
            if (txt.codePointAt(i) > 255) {
                return false;
            }
        }
        return true;
    }

    private static String correctAtiehPhoneNo(String in) {
        in = in.trim();
        if (in.startsWith("+")) {
            in = in.substring(1);
        }
        while (in.startsWith("0")) {
            in = in.substring(1);
        }
        if (!in.startsWith("98")) {
            in = "98" + in;
        }
        return in;
    }
}