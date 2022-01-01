import com.vx6.master.MasterVerticle;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;

import java.util.Date;
import java.util.List;

public class CompleteSmsInfo extends MasterVerticle {
    private SharedData sharedData;
    private JsonObject constants = new JsonObject();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        constants = (JsonObject) sharedData.getLocalMap("SAPTA_CONSTANTS").get("AYN");
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.getJsonObject("smsInfo");
        Date date = new Date();
        String txnPDate = PersianCalendarBean.getPersianDateSimple(date).substring(0, 8);
        String txnTime = PersianCalendarBean.getPersianDateSimple(date).substring(8, 14);
        smsInfo.put("txnPDate", txnPDate);
        smsInfo.put("txnTime", txnTime);
        smsInfo.put("transGroup", constants.getString("transGroup"));
        smsInfo.put("bankId", constants.getString("bankId"));
        //body.mergeIn(this.constants);
        //body.put("constants", this.constants);
        //this.setting.mergeIn(body);
        /*if (body.containsKey("PHONENO"))
            body.put("phoneNos", new JsonArray(List.of(body.getString("PHONENO"))));
        else
            body.put("phoneNos", new JsonArray());*/

        body.put("txDT", date.toInstant());
        body.put("processDT", date.toInstant());
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    }
}