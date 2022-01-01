import com.vx6.master.MasterVerticle;
import com.vx6.tools.PatternsArrayList;
//import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
//import io.vertx.core.impl.future.PromiseImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class SmsProviderDispatcher extends MasterVerticle {
    public ArrayList<PatternsArrayList> filters = new ArrayList<>();
    private long curSendRefNo2 = 0;
    //private Future<Void> future;
    //private Promise<Long> promise = Promise.promise();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        setting.getJsonArray("filters").forEach(f -> {
            JsonObject filter = (JsonObject) f;
            filters.add(new PatternsArrayList(filter.getJsonArray("allow"), filter.getJsonArray("deny")));
        });
        /*Future future = promise.future();
        future.onComplete(res-> {
            System.out.println(((PromiseImpl) res).result());
        });
        future.onFailure(res->{
            eb.publish(addressBook.getError(), "Failed to get \"MaxSendRefNo2\"");
            errorOutboundCount++;
        });*/
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject smsInfo = body.getJsonObject("smsInfo");
        JsonArray phoneNos = new JsonArray(List.of(smsInfo.getString("phoneNos")));
        phoneNos.forEach(phoneNo -> {
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i).allow((String) phoneNo)) {
                    publishOut(i, body.copy(), addressBook.getDeliveryOptions(msg)
                            .addHeader("phoneNo", (String) phoneNo)
                            .addHeader("sendRefNo2", ++curSendRefNo2 + ""));
                    break;//If there is more provider to send then "comment out" the break!
                }
            }
        });
    }

    @Override
    public <T> void unknownCmd(Message<T> tMessage, String cmd) {
        if (cmd.equalsIgnoreCase("MaxSendRefNo2")) {
            this.curSendRefNo2 = Long.parseLong(((JsonObject) tMessage.body()).getString("MaxSendRefNo2"));
            System.out.println("curSendRefNo2 = " + curSendRefNo2);
            //promise.complete(curSendRefNo2);
        }
    }
}