//import com.ghasemkiani.util.icu.PersianCalendar;

import com.ceramic.tools.Account;
import com.vx6.master.MasterVerticle;
//import com.vx6.utils.Library;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
//import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
//import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;

//import static com.vx6.utils.Library.correctPhoneNos;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class AddCreditItemBusiness extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, String> accounts;
    private LocalMap<String, String> customers;
    private JsonObject constants;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat fsdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Account acc = new Account();

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        this.resultOutboundCount--;//Here we may send multiple messages so default assumption of just one output message must be draw-back
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");

        String accountNo = tran.getString("ACCOUNTNO");
        if (isBlank(accountNo)) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("ERROR", "AccountNo is blank"));
            this.errorOutboundCount++;
            return;
        }

        String creditName = tran.getString("CREDITITEMNAME");
        if (isBlank(creditName)) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("ERROR", "creditName is blank"));
            this.errorOutboundCount++;
            return;
        }

        String customerId = tran.getString("CUSTOMERID") == null ? "" : tran.getString("CUSTOMERID").trim();
        if (isBlank(customerId)) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("ERROR", "CustomerId is blank"));
            this.errorOutboundCount++;
            return;
        }
        /*String expireDate = tran.getString("EXPIREDATE") == null ? "" : tran.getString("EXPIREDATE").trim();
        if (expireDate.startsWith("00000000"))
            expireDate = "";
        else if (expireDate.startsWith("14000000"))
            expireDate = "";
        else {
            if (expireDate.startsWith("00")) {
                int i = Integer.parseInt(expireDate.substring(2, 4));
                if (i >= 90) {
                    expireDate = "13" + expireDate.substring(2);
                } else {
                    expireDate = "14" + expireDate.substring(2);
                }
            }
        }
*/
        String branchNo = tran.getString("BRANCH");

        //List<String> beforePhones = new ArrayList<>();
        JsonObject accountInfo = new JsonObject();

        if (body.containsKey("accountInfo")) {
            accountInfo = body.getJsonObject("accountInfo").copy();
            /*beforePhones.addAll(Arrays.asList(accountInfo.getString("Phones")
                    .replace(",", ";").split(";")));*/
        } else {
            accountInfo.put("AccountNo", accountNo);
            accountInfo.put("CustomerId", customerId);
            accountInfo.put("Phones", "");
            accountInfo.put("RemainAmount", 0);
            accountInfo.put("RemainDays", 0);
            accountInfo.put("DueDate", null);
            accountInfo.put("LowLimitDebit", constants.getInteger("defaultLowLimitDebit") + "");
            accountInfo.put("LowLimitCredit", constants.getInteger("defaultLowLimitCredit") + "");
            accountInfo.put("CreditItem", constants.getString("startupCreditItem"));
            accountInfo.put("NearExpireNotifyDate", null);
            accountInfo.put("ExpiredNotifyDate", null);
            accountInfo.put("EditDT", fsdf.format(new Date()));
            accountInfo.put("AutoRenewRegCancelNotifyDate", null);
            accountInfo.put("ExpireDate", "");
            accountInfo.put("AutoRenewRegister", true);
            accountInfo.put("TransGroupsStr", constants.getString("transGroup"));
        }
        //accountInfo.put("Phones", phoneNo);
        if (isNotBlank(accountInfo.getString("Phones"))) {
            accountInfo.put("Enable", true);
        }
        var accountNos = customers.getOrDefault(customerId, "");

        if (constants.getBoolean("customerBase")) {
            if (constants.getString("bankId").equalsIgnoreCase("AYN") && isNotBlank(customerId)) {
                //Set all accounts (with respect to this customer id) to the biggest dueDate.
                //First of first specify biggest dueDate
                var dueDate = "";
                String[] accNos = Arrays.stream(accountNos.split("\\|")).filter(acc -> acc.trim().length() > 9).toArray(String[]::new);
                for (String accNo : accNos) {
                    JsonObject account = acc.csv2json(accounts.getOrDefault(accNo, null));
                    if (account != null && account.getString("DueDate", "").compareTo(dueDate) > 0) {
                        dueDate = account.getString("DueDate");
                    }
                }

                //Now we have biggest dueDate, and it's time to set all accounts dueDate to that.
                try {
                    Date date;
                    if (isBlank(dueDate) || (new Date().getTime() > sdf.parse(dueDate).getTime())) {
                        date = new Date(new Date().getTime() + (long) (1000 * 60 * 60 * 24)
                                * constants.getJsonObject("creditItem").getJsonObject(creditName).getInteger("days"));
                    } else {
                        date = new Date(sdf.parse(dueDate).getTime() + (long) (1000 * 60 * 60 * 24)
                                * constants.getJsonObject("creditItem").getJsonObject(creditName).getInteger("days"));
                    }
                    dueDate = PersianCalendarBean.getLastDayOfMonthOfPersianDate(date);
                    dueDate = sdf.format(PersianCalendarBean.getJulianDate(dueDate, "000000"));
                    for (String accNo : accNos) {
                        JsonObject account = acc.csv2json(accounts.getOrDefault(accNo, null));
                        if (!(account == null || dueDate.equalsIgnoreCase(account.getString("DueDate"))
                                || accountNo.equalsIgnoreCase(account.getString("AccountNo")))) {
                            JsonObject oldAccount = account.copy();
                            account.put("DueDate", dueDate);
                            account.put("EditDT", fsdf.format(new Date()));
                            accounts.put(accNo, acc.json2csv(account));
                            //Save updated account on DB
                            eb.publish(addressBook.getResult(),
                                    new JsonObject().put("accountInfo", oldAccount).put("newAccountInfo", account),
                                    addressBook.getDeliveryOptions(msg));
                            resultOutboundCount++;
                            //TODO: This the right place to send sms to account owner if we want to.
                        }
                    }
                    accountInfo.put("DueDate", dueDate);
                } catch (Exception e) {
                    sendException(e);
                }
            }
        } else {
            var dueDate = accountInfo.getString("DueDate");
            try {
                Date date;
                if (isBlank(dueDate) || (new Date().getTime() > sdf.parse(dueDate).getTime())) {
                    date = new Date(new Date().getTime() + (long) (1000 * 60 * 60 * 24)
                            * constants.getJsonObject("creditItem").getJsonObject(creditName).getInteger("days"));
                } else {
                    date = new Date(sdf.parse(dueDate).getTime() + (long) (1000 * 60 * 60 * 24)
                            * constants.getJsonObject("creditItem").getJsonObject(creditName).getInteger("days"));
                }
                accountInfo.put("DueDate", sdf.format(date));
            } catch (Exception e) {
                sendException(e);
            }
        }

        if (!accountNos.contains(accountNo)) {
            accountNos += "|" + accountNo;
            customers.put(customerId, accountNos);
        }
        accountInfo.put("EditDT", fsdf.format(new Date()));
        accounts.put(accountNo, acc.json2csv(accountInfo));
        //Save on DB
        eb.publish(addressBook.getResult(), body.put("newAccountInfo", accountInfo), addressBook.getDeliveryOptions(msg));
        resultOutboundCount++;
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        super.ready(tMessage);
        accounts = sharedData.getLocalMap("SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("SAPTA_CUSTOMERS");
        constants = config().getJsonObject("dataSource").getJsonObject("Config");
    }
}