import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.util.Random;

public class SampleVerticle1 extends MasterVerticle {
    private JsonObject joStat = new JsonObject();
    private Integer overAllCount = 0, billPaidCount = 0;

    @Override
    public void process(Message msg) {
        JsonObject joBody = (JsonObject) msg.body();
        overAllCount++;
        if (joBody.getString("TRANSACTION_NAME").equalsIgnoreCase("BillPaid")) {
            billPaidCount++;
            String billType = joBody.getString("Bill_Type").trim();
            if (billType.length() > 0) {
                Integer count = joStat.getInteger(billType, 0);
                joStat.put(billType, ++count);
            }
        } else {
            eb.publish(addressBook.getError(), msg.body(), addressBook.getDeliveryOptions(msg).addHeader("error", "NotBillPaid"));
        }
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        DeliveryOptions dO = addressBook.getDeliveryOptions(tMessage);
        dO.addHeader("OverAllCount", overAllCount + "").addHeader("BillPaidCount", billPaidCount + "");
        eb.publish(addressBook.getResult(), joStat, dO);
    }
}