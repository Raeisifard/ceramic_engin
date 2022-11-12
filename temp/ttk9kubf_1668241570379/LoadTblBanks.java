
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.time.StopWatch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Set;

public class LoadTblBanks extends MasterVerticle {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private SharedData sharedData;
    private LocalMap<String, String> banks;
    private long banksCount = 0L;
    private static JsonObject obrs_db;
    private static String label = "<h3 style=\"margin: 0;\">LoadTblBanks</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">Banks Read:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: %s\">%s</h2>";
    private static ArrayList<String> banksList = new ArrayList<>();

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
        banks = sharedData.getLocalMap("SAPTA_BANKS");
        banksList.clear();
        sendLabels();
    }

    private void sendLabels() {
        sendLabel(String.format(label, banksCount == 0 ? "red" : "#0043ff", banksCount), "18ff96", "50", "efff18");
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        banksCount = 0;
        banksList.clear();
        String SQL = "SELECT * FROM tblBanks";
        String connectionUrl = "jdbc:sqlserver://" + obrs_db.getString("ip") + ":" + obrs_db.getInteger("port") +
                ";responseBuffering=adaptive;DatabaseName=" + obrs_db.getString("dbName") + ";user=" +
                obrs_db.getString("user") + ";password=" + obrs_db.getString("pass");
        try (Connection con = DriverManager.getConnection(connectionUrl);
             Statement stmt = con.createStatement();) {
            StopWatch watch = new StopWatch();
            System.out.println("Loading banks table started!");
            watch.start();
            ResultSet rs = stmt.executeQuery(SQL);
            StringBuilder remainder = new StringBuilder();
            while (rs.next()) {
                String bankCode = rs.getString("bankCode").trim();
                String bankName = rs.getString("bankName").trim();
                banks.put(bankCode, bankName);
                banksList.add(bankCode);
                banksCount++;
            }
            sendLabels();
            con.close();
            Set keySet = this.banks.keySet();
            keySet.forEach(key -> {
                if (!banksList.contains(key))
                    banks.remove(key);
            });
            watch.stop();
            long millis = watch.getTime();
            long minutes = (millis / 1000) / 60;
            int seconds = (int) ((millis / 1000) % 60);
            System.out.println("Banks loaded. count: " + banksCount + " time elapsed: " + minutes + ":" + seconds);
            eb.publish(addressBook.getResult(), new JsonObject().put("msg", "result-set-end"),
                    addressBook.getDeliveryOptions().addHeader("cmd", "status").addHeader("name", "banks"));
            resultOutboundCount++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}