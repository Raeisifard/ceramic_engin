import com.ceramic.tools.Account;
import com.vx6.master.MasterVerticle;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class FeeDeductionBusiness extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, JsonObject> cache;
    private LocalMap<String, String> accounts;
    private static JsonObject constants;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat compaqDateTime = new SimpleDateFormat("yyyyMMddHHmmss");

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        this.eb.consumer(config.getString("feeBusinessAddress"), this::doBusiness);
        sharedData = vertx.sharedData();
        initPromise.complete();
    }

    private <T> void doBusiness(Message<T> tMessage) {
        //Todo imply FeeDeductionBusiness
        JsonObject body = (JsonObject) tMessage.body();
        String status = tMessage.headers().get("status");
        System.out.println("FeeDeductionBusiness: " + body);
        System.out.println("FeeDeductionBusiness: " + tMessage.headers());
        if (status.equals("SUCCESS")) {
            body.getJsonObject("accountInfo").put("CreditItem", constants.getString("startupCreditItem"));
            JsonObject newAccountInfo = body.getJsonObject("accountInfo").copy();
            Date dueDate = null;
            try {
                dueDate = sdf.parse(newAccountInfo.getString("DueDate"));
            } catch (ParseException e) {
                try {
                    dueDate = sdf.parse(sdf.format(new Date()));
                } catch (ParseException ignore) {
                }
            }
            JsonObject creditItem = constants.getJsonObject("creditItem").getJsonObject(newAccountInfo.getString("CreditItem"));
            if ((dueDate == null) || (new Date().getTime() > dueDate.getTime()))
                dueDate = new Date(new Date().getTime() + (long) (1000 * 60 * 60 * 24) * creditItem.getInteger("days"));
            else
                dueDate = new Date(dueDate.getTime() + (long) (1000 * 60 * 60 * 24) * creditItem.getInteger("days"));
            newAccountInfo.put("DueDate", sdf.format(dueDate));
            newAccountInfo.put("Enable", newAccountInfo.getString("Phones") != null && newAccountInfo.getString("Phones").trim().length() > 0);
            newAccountInfo.put("NearExpireNotifyDate", null);
            newAccountInfo.put("ExpiredNotifyDate", null);
            newAccountInfo.put("AutoRenewRegCancelNotifyDate", null);
            newAccountInfo.put("AutoRenewRegister", true);
            accounts.put(newAccountInfo.getString("AccountNo"), Account.json2csv(newAccountInfo));
            JsonObject smsInfo = new JsonObject();
            smsInfo.put("sourceName", "DepositResponse")
                    .put("channel", "Interprocess|WSRegister")
                    .put("transId", "WSDepositSuccess")
                    .put("accountNo", newAccountInfo.getString("AccountNo"))
                    .put("dueDate", Objects.requireNonNull(PersianCalendarBean.getPersianDateSimple(dueDate)).substring(0, 8))
                    .put("amount", body.getInteger("amount"))
                    .put("phoneNos", newAccountInfo.getString("Phones"))
                    .put("uniqKey", newAccountInfo.getString("AccountNo") + compaqDateTime.format(new Date()) + smsInfo.getString("transId"));
            body.put("newAccountInfo", newAccountInfo).put("smsInfo", smsInfo);
            publishOut(0, body, addressBook.getDeliveryOptions(tMessage));
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(tMessage));
            resultOutboundCount++;
        } else if (status.equals("ERROR")) {
            JsonObject newAccountInfo = body.getJsonObject("accountInfo").copy();
            newAccountInfo.put("AutoRenewRegister", false);
            accounts.put(newAccountInfo.getString("AccountNo"), Account.json2csv(newAccountInfo));
            JsonObject smsInfo = new JsonObject();
            smsInfo.put("sourceName", "DepositResponse")
                    .put("channel", "Interprocess|WSRegister")
                    .put("transId", "WSDepositError")
                    .put("accountNo", newAccountInfo.getString("AccountNo"))
                    .put("amount", body.getInteger("amount"))
                    .put("phoneNos", newAccountInfo.getString("Phones"))
                    .put("uniqKey", newAccountInfo.getString("AccountNo") + compaqDateTime.format(new Date()) + smsInfo.getString("transId"));
            try {
                smsInfo.put("dueDate", Objects.requireNonNull(PersianCalendarBean.getPersianDate(sdf.parse(newAccountInfo.getString("DueDate")))).substring(0, 10));
            } catch (Exception ignore) {
            }
            body.put("newAccountInfo", newAccountInfo).put("smsInfo", smsInfo);
            publishOut(0, body, addressBook.getDeliveryOptions(tMessage));
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(tMessage));
            resultOutboundCount++;
        }
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        super.ready(tMessage);
        accounts = sharedData.getLocalMap("SAPTA_ACCOUNTS_3");
        constants = config().getJsonObject("dataSource").getJsonObject("Config");
    }
}