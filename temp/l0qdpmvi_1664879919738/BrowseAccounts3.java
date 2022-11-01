import Utils.Library;
import com.ceramic.tools.Account;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;

import java.text.SimpleDateFormat;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class BrowseAccounts3 extends MasterVerticle {
    private SharedData sharedData;
    private LocalMap<String, String> accounts;
    private boolean browse = false;
    private int count = 0, loc = 0;
    private String[] keySet = new String[0];
    private String datePattern = "yyyyMMdd";
    private SimpleDateFormat simpleDateFormat;
    private long nextDate;
    private JsonObject account;
    private int feeDeduct = 0, notDeduct = 0;
    private static String label = "<h3 style=\"margin: 0;\">BrowseAccounts3</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">Fee Deduct.:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: blue\">%s</h2>" +
            "<div><h3 style=\"display: inline-block; margin: 0;\">Not Deduct.:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: green\">%s</h2></div>";


    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        holdOn = false;
        sharedData = vertx.sharedData();
        accounts = sharedData.getLocalMap("SAPTA_ACCOUNTS_3");
        simpleDateFormat = new SimpleDateFormat(datePattern);
        sendLabels();
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        if (body.containsKey("cmd") && body.getString("cmd").equalsIgnoreCase("browse")) {
            browse = true;
            if (accounts.size() > 0)
                vertx.executeBlocking(promise -> {
                    String[] keySet = accounts.keySet().toArray(String[]::new);
                    promise.complete(keySet);
                }, res -> {
                    keySet = (String[]) res.result();
                });
            loc = 0;
        }
        nextDate = Long.parseLong(simpleDateFormat.format(Library.nextDay(setting.getInteger("advanceDay"))));
        System.out.println("nextDate: " + nextDate);
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        if (!holdOn && tMessage.body() instanceof JsonObject) {
            JsonObject body = (JsonObject) tMessage.body();
            if (body.containsKey("count")) {
                count = body.getInteger("count");
                if (loc < keySet.length - 1)
                    browseAccount3();
            }
        }
        sendLabels();
    }

    private void browseAccount3() {
        if (browse && count > 0) {
            while (count > 0) {
                String strAccount = keySet[loc++];
                String strAccount3 = accounts.get(strAccount);
                System.out.println(strAccount3);
                if (isNotBlank(strAccount3)) {
                    account = Account.csv2json(strAccount3);
                    assert account != null;
                    if (shouldFeeDeduction(account)) {
                        System.out.println("Should deduct: " + account.getString("AccountNo"));
                        eb.publish(addressBook.getResult(), new JsonObject().put("accountInfo", account));
                        resultOutboundCount++;
                        feeDeduct++;
                        --count;
                    } else
                        System.out.println("Should not deduct: " + account.getString("AccountNo"));
                    notDeduct++;
                }
                if (loc == keySet.length) {
                    browse = false;
                    eb.publish(addressBook.getError(), "Browsing finished for " + (loc) + " records.", addressBook.getDeliveryOptions().addHeader("status", "BROWSING_FINISHED"));
                    keySet = new String[0];
                    errorOutboundCount++;
                    return;
                }
            }
        }
    }

    private boolean shouldFeeDeduction(JsonObject account) {
        if (account.getBoolean("Enable") && account.getBoolean("AutoRenewRegister")) {
            try {
                String dueD = account.getString("DueDate");
                if (dueD == null)
                    dueD = "99999999";
                long dueDate = Long.parseLong(dueD.replace("-", ""));
                return dueDate < nextDate;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    protected <T> void control(Message<T> tMessage) {
        String cmd = tMessage.body().toString().toLowerCase();
        switch (cmd) {
            case "pause":
                this.holdOn = true;
                break;
            case "resume":
                this.holdOn = false;
                break;
            default:
                controlDefault(tMessage);
                break;
        }
    }

    private void sendLabels() {
        sendLabel(String.format(label, feeDeduct, notDeduct), "18ff96", "50", "efff18");
    }
}