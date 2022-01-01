
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
//import java.io.IOException;
//import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

//import com.microsoft.sqlserver.jdbc.SQLServerStatement;


public class SampleVerticle1 extends MasterVerticle {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private SharedData sharedData;
    private LocalMap<String, String> accounts;
    private LocalMap<String, String> customers;
    private long accountCount = 0L;
    private long customerCount = 0L;
    private static final String EB_ADDRESS = "/freeboard/sse/sapta/registery";
    private int retry = 500000;
    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        start();
        startPromise.complete();
        initialize();
        //startFuture.complete();
    }
    
    public void initialize() throws Exception {
        LOGGER.info("initialize verticle {" + this + "}");
        sharedData = vertx.sharedData();
        accounts = sharedData.getLocalMap("AYN_SAPTA_ACCOUNTS_3");
        customers = sharedData.getLocalMap("AYN_SAPTA_CUSTOMERS");
        accounts.clear();
        customers.clear();
        long timerID = vertx.setPeriodic(5000, id -> {
            metrics();
        });
        String some_val = "my_val";
        LOGGER.info("this is how you inject {} into your statement.", some_val);
    }

    private boolean extracted(String accountInfo) {
        accountCount++;
        var split = accountInfo.split("\\|");
        //var accountNo = accountInfo.substring(0, 13);
        if (split.length < 15) {
            System.out.println("Defective: " + accountInfo);
            return true;
        }
        var accountNo = split[0];
        accounts.put(accountNo, accountInfo);
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
        String SQL = "select \n" +
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
                "\tfrom [TblAccounts6] for XML PATH('')";
        //SQL = "select 'behnam'";
        // Create a variable for the connection string.
        String connectionUrl = "jdbc:sqlserver://192.168.166.11:1433;responseBuffering=adaptive;DatabaseName=BXX_SAPTA11;user=sa;password=suny$123";

        try (Connection con = DriverManager.getConnection(connectionUrl);
             Statement stmt = con.createStatement();) {

            // In adaptive mode, the application does not have to use a server cursor
            // to avoid OutOfMemoryError when the SELECT statement produces very large
            // results.

            // Display the response buffering mode.
           /* SQLServerStatement SQLstmt = (SQLServerStatement) stmt;
            System.out.println("Response buffering mode is: " + SQLstmt.getResponseBuffering());*/
            //SQLstmt.close();
            //System.out.println("SQL: " + SQL);
            // Get the updated data from the database and display it.
            StopWatch watch = new StopWatch();
            System.out.println("Loading DB started!");
            watch.start();
            ResultSet rs = stmt.executeQuery(SQL);
            StringBuilder remainder = new StringBuilder();
            while (rs.next()) {
                //extracted();
                //System.out.println("rs.next()");
                Reader reader = rs.getCharacterStream(1);
                if (reader != null) {
                    char[] ac3 = new char[2048];
                    while (reader.read(ac3) != -1) {
                        //System.out.println(accounts.size());
                        remainder.append(String.valueOf(ac3));
                        //System.out.println(remainder.toString());
                        String[] rows = remainder.toString().split("\\\\n");
                        int len = rows.length - 1;
                        if (len > 0) {
                            for (int i = 0; i < len; i++) {
                                //System.out.println("Account: " + rows[i]);

                                // System.out.println(row.toString());
                                // LOGGER.debug( row.toString());
                                var accountInfo = rows[i];
                                if (extracted(accountInfo)) continue;
                            }
                            //output = new char[2048];
                        }
                        remainder.setLength(0);
                        remainder.append(rows[len].trim());
                    }
                    //System.out.println(rs.getString(1) + " has been accessed for the summary column.");
                    // Close the stream.
                    reader.close();
                }
            }
            if (remainder.toString().trim().length() > 0) {
                System.out.println("Remainder: " + remainder.toString());
                extracted(remainder.toString());
            }
            watch.stop();
            long millis = watch.getTime();  // obtained from StopWatch
            long minutes = (millis / 1000)  / 60;
            int seconds = (int)((millis / 1000) % 60);
            System.out.println("Time Elapsed: " + minutes + ":" + seconds); // Prints: Time Elapsed: 2501
        } // Handle any errors that may have occurred.
        catch (SQLException | IOException e) {
            e.printStackTrace();
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