import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.StringUtils;

public class FindAccountInfo extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, JsonObject> accounts;
    private LocalMap<String, JsonArray> customers;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        accounts = sharedData.getLocalMap("AYN_SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("AYN_SAPTA_CUSTOMERS");
        initPromise.complete();
    }
    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        var accountNo = tran.getString("ACCOUNTNO");
        if (StringUtils.isNotBlank(accountNo) && accounts.containsKey(accountNo)){
            JsonObject accountInfo = accounts.get(accountNo);
            body.put("accountInfo", accountInfo);
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
        }else{
            resultOutboundCount--;
            publishOut(0, body, addressBook.getDeliveryOptions(msg));
        }
    }
}