import com.vx6.master.MasterVerticle;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.shareddata.SharedData;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;

import java.util.Date;
import java.util.Objects;

public class CompleteSmsInfo extends MasterVerticle {
    private JsonObject constants;
    private SharedData sharedData;
    private LocalMap<String, JsonObject> patterns;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        patterns = sharedData.getLocalMap("json_patterns");
        initPromise.complete();
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        super.ready(tMessage);
        constants = config().getJsonObject("dataSource").getJsonObject("Config");
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.containsKey("smsInfo") ? body.getJsonObject("smsInfo") : body.getJsonObject("tran");
        Date date = new Date();
        String txnPDate = Objects.requireNonNull(PersianCalendarBean.getPersianDateSimple(date)).substring(0, 8);
        String txnTime = Objects.requireNonNull(PersianCalendarBean.getPersianDateSimple(date)).substring(8, 14);
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
        JsonObject pattern = patterns.get(smsInfo.getString("transId"));
        if (pattern != null) {
            body.put("pattern", pattern);
        }
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
    }
}