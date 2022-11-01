import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class LoadAccountDB extends MasterVerticle {
    private static final String Cache_Name = "json_sql_patterns";
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private SharedData sharedData;
    private LocalMap<String, JsonObject> cache;
    private LocalMap<String, String> accounts;
    private LocalMap<String, String> customers;
    private long accountCount = 0L;
    private long customerCount = 0L;
    private static final String EB_ADDRESS = "/freeboard/sse/sapta/registery";
    private int retry = 500000;
    private static JsonObject obrs_db;
    private static Set<String> accountsSet = new HashSet<String>();
    private static String label = "<h3 style=\"margin: 0;\">LoadAccountDB</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">Accounts Read:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>" +
            "<div><h3 style=\"display: inline-block; margin: 0;\">Customers Count:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2></div>" +
            "<div><h3 style=\"display: inline-block; margin: 0;\">Accounts List Size:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2></div>";

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        start();
        if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("OBRS-DB")))
            startPromise.fail("OBRS-DB data not found on datasource");
        else {
            obrs_db = config().getJsonObject("dataSource").getJsonObject("OBRS-DB");
            startPromise.complete();
            initialize();
        }
        healthCheck();
    }

    public void initialize() throws Exception {
        LOGGER.info("initialize verticle {" + this + "}");
        sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap(Cache_Name);
        accounts = sharedData.getLocalMap("SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("SAPTA_CUSTOMERS");
        sendLabels();
    }

    @Override
    public <T> void process(Message<T> tMessage) {
        resultOutboundCount--;
        if (tMessage.headers().contains("cmd") && tMessage.headers().get("cmd").equalsIgnoreCase("status") &&
                tMessage.headers().contains("name") && tMessage.headers().get("name").equalsIgnoreCase("sql") &&
                accounts.size() == 0) {
            noCmd(tMessage, "");
        }
    }

    private void sendLabels() {
        sendLabel(String.format(label, accountCount == 0 ? "red" : "#0043ff", accountCount,
                customers.size() == 0 ? "red" : "#0043ff", customers.size(),
                accounts.size() == 0 ? "red" : "#0043ff", accounts.size()), "18ff96", "50", "efff18");
    }

    private boolean extraction(String accountInfo) {
        accountCount++;
        if (accountCount % 500000 == 0)
            sendLabels();
        var split = accountInfo.split("\\|");
        if (split.length < 15) {
            System.out.println("Defective: " + accountInfo);
            return true;
        }
        var accountNo = split[0];
        accounts.put(accountNo, accountInfo);
        //System.out.println("account: " + accountInfo);
        accountsSet.add(accountNo);
        var customerId = split[2];
        if (isNotBlank(customerId)) {
            var accountNos = customers.getOrDefault(customerId, "");
            if (!accountNos.contains(accountNo)) {
                accountNos += "|" + accountNo;
                customers.put(customerId, accountNos);
            }
        }
        return false;
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        if (cache.containsKey("accounts")) {
            var wasAccountsFull = accounts.size() > 0;
            accountsSet.clear();
            /*String SQL = "select \n" +
                    "\t--convert(varchar(9),UniqId) + '|' +   \n" +
                    "\tconvert(varchar(13),isnull(AccountNo,'null')) + '|' +\n" +
                    "\tconvert(varchar(10),isnull(Enable,'null')) + '|' +\n" +
                    "\tconvert(varchar(10),isnull(CustomerId,'null')) + '|' +\n" +
                    "\tconvert(varchar(100),isnull(TransGroupsStr,'null')) + '|' +\n" +
                    "\tconvert(varchar(110),isnull(Phones,'null')) + '|' +\n" +
                    "\tconvert(varchar(10),isnull(AutoRenewRegister,'null')) + '|' +\n" +
                    "\tconvert(varchar(60),isnull(RemainAmount,'null')) + '|' +\n" +
                    "\tconvert(varchar(50),isnull(RemainDays,'null')) + '|' +\n" +
                    "\tisnull(convert(varchar(10),DueDate, 102),'null') + '|' +\n" +
                    "\tconvert(varchar(10),isnull(LowLimitCredit,'null')) + '|' +\n" +
                    "\tconvert(varchar(10),isnull(LowLimitDebit,'null')) + '|' +\n" +
                    "\tisnull(convert(varchar(10),ExpireDate, 102) , 'null')+ '|' +\n" +
                    "\tisnull(convert(varchar(10),NearExpireNotifyDate, 102),'null')+ '|' +\n" +
                    "\tisnull(convert(varchar(10),ExpiredNotifyDate, 102) , 'null') + '|' +\n" +
                    "\t--convert(varchar(23),isnull(EditDT,'null')) + '|' +\n" +
                    "\tisnull(convert(varchar(10),AutoRenewRegCancelNotifyDate, 102) , 'null') +'\\n'\n" +
                    "\t--convert(varchar(10),isnull(Flag,'null'))\n" +
                    "\tfrom [TblAccounts3] for XML PATH('')";*/
            String SQL = cache.get("accounts").getString("text");
            String connectionUrl = "jdbc:sqlserver://" + obrs_db.getString("ip") + ":" + obrs_db.getInteger("port") +
                    ";responseBuffering=adaptive;DatabaseName=" + obrs_db.getString("dbName") + ";user=" +
                    obrs_db.getString("user") + ";password=" + obrs_db.getString("pass");

            try (Connection con = DriverManager.getConnection(connectionUrl);
                 Statement stmt = con.createStatement();) {
                StopWatch watch = new StopWatch();
                System.out.println("Loading DB started!");
                watch.start();
                ResultSet rs = stmt.executeQuery(SQL);
                StringBuilder remainder = new StringBuilder();
                while (rs.next()) {
                    Reader reader = rs.getCharacterStream(1);
                    if (reader != null) {
                        char[] ac3 = new char[2048];
                        while (reader.read(ac3) != -1) {
                            remainder.append(String.valueOf(ac3));
                            String[] rows = remainder.toString().split("\\^");
                            int len = rows.length - 1;
                            if (len > 0) {
                                for (int i = 0; i < len; i++) {
                                    var accountInfo = rows[i];
                                    extraction(accountInfo);
                                }
                            }
                            remainder.setLength(0);
                            remainder.append(rows[len].trim());
                        }
                        reader.close();
                    }
                }
                if (remainder.toString().trim().length() > 0) {
                    System.out.println("Remainder: " + remainder.toString());
                    extraction(remainder.toString());
                }
                con.close();
                if (wasAccountsFull) {
                    Set<String> keySet = accounts.keySet();
                    keySet.forEach(key -> {
                        if (!accountsSet.contains(key))
                            accounts.remove(key);
                    });
                }
                accountsSet.clear();
                sendLabels();
                watch.stop();
                long millis = watch.getTime();  // obtained from StopWatch
                long minutes = (millis / 1000) / 60;
                int seconds = (int) ((millis / 1000) % 60);
                System.out.println("Time Elapsed: " + minutes + ":" + seconds); // Prints: Time Elapsed: 2501
                eb.publish(addressBook.getResult(), new JsonObject().put("msg", "AccountDB loaded").put("cmd", "browse"),
                        addressBook.getDeliveryOptions().addHeader("cmd", "status").addHeader("name", "account"));
                resultOutboundCount++;
                System.out.println("accountCount: " + accountCount);
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void metrics() {
        eb.publish(EB_ADDRESS,
                new JsonObject()
                        .put("accountCount", accountCount)
                        .put("customerCount", customers.size())
                        .put("accountSize", accounts.size())
                , addressBook.getDeliveryOptions().addHeader("event", "initialize")
                        .addHeader("retry", this.retry + ""));
    }
}