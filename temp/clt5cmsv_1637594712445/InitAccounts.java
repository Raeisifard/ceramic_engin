import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class InitAccounts extends MasterVerticle {
    private long accountCount = 0L;
    private long customerCount = 0L;
    private static final String EB_ADDRESS = "/freeboard/sse/sapta/registery";
    private int retry = 500000;
    private SharedData sharedData;
    private LocalMap<String, JsonObject> accounts;
    private LocalMap<String, JsonArray> customers;
    private LocalMap<String, String> accountsString;
    
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        accounts = sharedData.getLocalMap("AYN_SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("AYN_SAPTA_CUSTOMERS");
        accountsString = sharedData.getLocalMap("AYN_SAPTA_ACCOUNTS_3_STRING");
        accounts.clear();
        customers.clear();
        accountsString.clear();
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        JsonObject body = ((JsonObject) msg.body()).copy();
        accountCount++;
        var accountNo = body.getString("AccountNo");
        if (StringUtils.isNotBlank(accountNo)) {
            body.put("Phones", Library.correctPhoneNos(body.getString("Phones")));
            accounts.put(accountNo, body);
            accountsString.put(accountNo, body.toString());
            System.out.println("msg: " + body.toString());
            var customerId = body.getString("CustomerId");
            if (StringUtils.isNotBlank(customerId)) {
                var accountNos = customers.getOrDefault(customerId, new JsonArray());
                if (!accountNos.contains(accountNo)) {
                    accountNos.add(accountNo);
                    customers.put(customerId, accountNos);
                }
            }
        }
    }

    @Override
    public void trigger(Message tMessage) {
        eb.publish(EB_ADDRESS,
                new JsonObject()
                        .put("accountCount", accountCount)
                        .put("customerCount", customers.size())
                        .put("accountSize", accounts.size())
                , addressBook.getDeliveryOptions().addHeader("event", "initialize")
                        .addHeader("retry", this.retry + "").addHeader("id", UUID.randomUUID().toString()));
    }
}