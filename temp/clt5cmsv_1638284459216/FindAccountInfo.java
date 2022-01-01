import com.ceramic.tools.Account;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
//import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.StringUtils;

public class FindAccountInfo extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, String> accounts;
    private LocalMap<String, String> customers;
    private static final Account acc = new Account();

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
        if (StringUtils.isNotBlank(accountNo) && accounts.containsKey(accountNo)) {
            String accountInfo = accounts.get(accountNo);
            body.put("accountInfo", acc.csv2json(accountInfo));
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
        } else {
            resultOutboundCount--;
            publishOut(0, body, addressBook.getDeliveryOptions(msg));
        }
    }

    /*private static JsonObject getJson(String acc) {
        var split = acc.split("//|");
        JsonObject accountInfo = new JsonObject();
        accountInfo.put("AccountNo", split[0].trim());
        accountInfo.put("Enable", "1".equals(split[1]));
        accountInfo.put("CustomerId", split[2]);
        accountInfo.put("TransGroupsStr", split[3]);
        accountInfo.put("Phones", split[4]);
        accountInfo.put("AutoRenewRegister", "1".equals(split[5]));
        accountInfo.put("RemainAmount", Long.parseLong(split[6]));
        accountInfo.put("RemainDays", Long.parseLong(split[7]));
        accountInfo.put("DueDate", split[8].equals("null") ? "" : split[8]);
        accountInfo.put("LowLimitCredit", Long.parseLong(split[9]));
        accountInfo.put("LowLimitDebit", Long.parseLong(split[10]));
        accountInfo.put("ExpireDate", split[11]);
        accountInfo.put("NearExpireNotifyDate", split[12]);
        accountInfo.put("ExpiredNotifyDate", split[13]);
        //accountInfo.put("EditDT", split[13]);
        accountInfo.put("AutoRenewRegCancelNotifyDate", split[14]);
        //accountInfo.put("Flag", split[15].equals("null") ? null : Integer.parseInt(split[15]));
        return accountInfo;
    }*/
}