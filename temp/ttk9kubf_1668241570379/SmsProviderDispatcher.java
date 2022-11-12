import com.vx6.master.MasterVerticle;
import com.vx6.tools.PatternsArrayList;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class SmsProviderDispatcher extends MasterVerticle {
    public ArrayList<PatternsArrayList> filters = new ArrayList<>();
    private JsonArray providers = new JsonArray();
    private long curSendRefNo2 = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        setting.getJsonArray("filters").forEach(f -> {
            JsonObject filter = (JsonObject) f;
            filters.add(new PatternsArrayList(filter.getJsonArray("allow"), filter.getJsonArray("deny")));
            providers.add(new JsonObject()
                    .put("providerName", filter.getString("name"))
                    .put("srcPhoneNo", filter.getInteger("srcPhoneNo")));
        });
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.containsKey("smsInfo") ? body.getJsonObject("smsInfo") : body.getJsonObject("tran");
        JsonArray phoneNos = new JsonArray(List.of(smsInfo.getString("phoneNos")));
        phoneNos.forEach(phoneNo -> {
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i).allow((String) phoneNo)) {
                    publishOut(i, body.copy(), addressBook.getDeliveryOptions(msg)
                            .addHeader("phoneNo", (String) phoneNo)
                            .addHeader("sendRefNo2", ++curSendRefNo2 + "")
                            .addHeader("providerName", providers.getJsonObject(i).getString("providerName"))
                            .addHeader("srcPhoneNo", providers.getJsonObject(i).getInteger("srcPhoneNo") + ""));
                    break;//If there is more provider to send then "comment out" the break!
                }
            }
        });
    }

    @Override
    public <T> void status(Message<T> msg) {
        if (msg.headers().contains("name") && msg.headers().get("name").equals("MaxSendRefNo2")) {
            this.curSendRefNo2 = Long.parseLong(((JsonObject) msg.body()).getString("MaxSendRefNo2"));
            System.out.println("curSendRefNo2 = " + curSendRefNo2);
        }
    }
}