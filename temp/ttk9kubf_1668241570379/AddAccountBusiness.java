//import com.ghasemkiani.util.icu.PersianCalendar;

import com.ceramic.tools.Account;
import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import com.vx6.utils.PersianCalendarBean;
import io.vertx.core.Promise;
//import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
//import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
//import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;

import static com.vx6.utils.Library.correctPhoneNos;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;


public class AddAccountBusiness extends MasterVerticle {
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
        this.resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        String accountNo = tran.getString("ACCOUNTNO");
        if (isBlank(accountNo)) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("ERROR", "AccountNo is blank"));
            this.errorOutboundCount++;
            return;
        }
        String phoneNo = correctPhoneNos(tran.getString("PHONENO"));
        if (isBlank(phoneNo)) {
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("ERROR", "PhoneNo is blank"));
            this.errorOutboundCount++;
            return;
        }

        String customerId = tran.getString("CUSTOMERID") == null ? "" : tran.getString("CUSTOMERID").trim();
        String expireDate = tran.getString("EXPIREDATE") == null ? "" : tran.getString("EXPIREDATE").trim();
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

        String branchNo = tran.getString("BRANCH");

        List<String> beforePhones = new ArrayList<>();
        JsonObject accountInfo = new JsonObject();

        if (body.containsKey("accountInfo")) {
            accountInfo = body.getJsonObject("accountInfo").copy();
            beforePhones.addAll(Arrays.asList(accountInfo.getString("Phones")
                    .replace(",", ";").split(";")));
        } else {
            accountInfo.put("AccountNo", accountNo);
            accountInfo.put("CustomerId", customerId);
            accountInfo.put("Phones", phoneNo);
            accountInfo.put("RemainAmount", 0);
            accountInfo.put("RemainDays", 0);
            accountInfo.put("DueDate", null);
            accountInfo.put("LowLimitDebit", constants.getInteger("defaultLowLimitDebit"));
            accountInfo.put("LowLimitCredit", constants.getInteger("defaultLowLimitCredit"));
            accountInfo.put("CreditItem", constants.getString("startupCreditItem"));
            accountInfo.put("NearExpireNotifyDate", null);
            accountInfo.put("ExpiredNotifyDate", null);
            accountInfo.put("EditDT", fsdf.format(new Date()));
            accountInfo.put("AutoRenewRegCancelNotifyDate", null);
        }
        accountInfo.put("AutoRenewRegister", true);
        accountInfo.put("TransGroupsStr", constants.getString("transGroup"));
        accountInfo.put("Phones", phoneNo);
        accountInfo.put("ExpireDate", expireDate);
        accountInfo.put("Enable", true);

        var accountNos = customers.getOrDefault(customerId, "");

        if (constants.getBoolean("customerBase") && isNotBlank(customerId)) {
            if (constants.getString("bankId").equalsIgnoreCase("AYN")) {
                //Set all accounts (with respect to this customer id) to the biggest dueDate.
                //First of first specify biggest dueDate
                var dueDate = "";
                String[] accNos = Arrays.stream(accountNos.split("\\\\|")).filter(acc -> acc.trim().length() > 9).toArray(String[]::new);
                //var accs = accountNos.split("\\\\|");
                for (String accNo : accNos) {
                    String account = accounts.getOrDefault(accNo.trim(), null);
                    if (account != null && account.split("\\\\|")[8].compareTo(dueDate) > 0) {
                        dueDate = account.split("\\\\|")[8];
                    }
                }
                //Now we have biggest dueDate, and It's time to set all accounts dueDate to that.
                if (isNotBlank(dueDate)) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Date date = sdf.parse(dueDate);
                        dueDate = PersianCalendarBean.getLastDayOfMonthOfPersianDate(date);
                        dueDate = sdf.format(PersianCalendarBean.getJulianDate(dueDate, "000000"));
                        for (String accNo : accNos) {
                            JsonObject account = acc.csv2json(accounts.getOrDefault(accNo.trim(), null));
                            if (!(account == null || dueDate.equalsIgnoreCase(account.getString("dueDate"))
                                    || accountNo.equalsIgnoreCase(account.getString("AccountNo")))) {
                                JsonObject oldAccount = account;
                                account.put("dueDate", dueDate);
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
        //Now its time to make some sms.
        List<String> afterPhones = new ArrayList<String>();
        afterPhones.addAll(Arrays.asList(accountInfo.getString("Phones")
                .replace(",", ";").split(";")));

        String newPhones = Library.myJoin(Library.moreItemsStringArrayList(afterPhones, beforePhones), ";");
        String deletedPhones = Library.myJoin(Library.moreItemsStringArrayList(beforePhones, afterPhones), ";");
        String keepPhones = Library.myJoin(Library.commonItemsStringArrayList(beforePhones, afterPhones), ";");

        if (!newPhones.isEmpty()) {
            JsonObject smsInfo = new JsonObject();
            smsInfo.put("channel", msg.headers().get("channel"));
            smsInfo.put("uniqKey", msg.headers().get("key"));
            smsInfo.put("transId", "NewPhones");
            smsInfo.put("transIndicator", "N");
            smsInfo.put("accountNo", accountNo);
            smsInfo.put("phoneNos", newPhones);
            smsInfo.put("freeMassage", true);
            smsInfo.put("branchNo", branchNo);
            smsInfo.put("userNo", tran.getString("USER"));
            smsInfo.put("terminalId", tran.getString("TERMINALID"));
            publishOut(0, new JsonObject().put("smsInfo", tran.copy().mergeIn(smsInfo)));
        }

        if (!deletedPhones.isEmpty()) {
            JsonObject smsInfo = new JsonObject();
            smsInfo.put("channel", msg.headers().get("channel"));
            smsInfo.put("uniqKey", msg.headers().get("key"));
            smsInfo.put("transId", "DeletePhones");
            smsInfo.put("transIndicator", "N");
            smsInfo.put("accountNo", accountNo);
            smsInfo.put("phoneNos", deletedPhones);
            smsInfo.put("freeMassage", true);
            smsInfo.put("branchNo", branchNo);
            smsInfo.put("userNo", tran.getString("USER"));
            smsInfo.put("terminalId", tran.getString("TERMINALID"));
            publishOut(0, new JsonObject().put("smsInfo", tran.copy().mergeIn(smsInfo)));
        }

        if (!keepPhones.isEmpty()) {
            JsonObject smsInfo = new JsonObject();
            smsInfo.put("channel", msg.headers().get("channel"));
            smsInfo.put("uniqKey", msg.headers().get("key"));
            smsInfo.put("transId", "KeepPhones");
            smsInfo.put("transIndicator", "N");
            smsInfo.put("accountNo", accountNo);
            smsInfo.put("phoneNos", keepPhones);
            smsInfo.put("freeMassage", true);
            smsInfo.put("branchNo", branchNo);
            smsInfo.put("userNo", tran.getString("USER"));
            smsInfo.put("terminalId", tran.getString("TERMINALID"));
            publishOut(0, new JsonObject().put("smsInfo", tran.copy().mergeIn(smsInfo)));
        }
    }

    @Override
    public <T> void ready(Message<T> tMessage) {
        super.ready(tMessage);
        accounts = sharedData.getLocalMap("SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("SAPTA_CUSTOMERS");
        constants = config().getJsonObject("dataSource").getJsonObject("Config");
    }
}