import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static com.vx6.utils.Library.correctPhoneNos;

public class CardCustomerCDC extends MasterVerticle {
    private Connection dbConnection = null;
    private String userName;
    private String password;
    private String url;
    private static JsonObject obrs_db;
    private Statement cmd;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        start();
        if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("OBRS-DB")))
            startPromise.fail("OBRS-DB data not found on datasource");
        else {
            obrs_db = config().getJsonObject("dataSource").getJsonObject("OBRS-DB");
            initialize(startPromise);
        }
        healthCheck();
    }

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("OBRS-DB"))) {
            initPromise.fail("OBRS-DB data not found on datasource");
            return;
        } else {
            obrs_db = config().getJsonObject("dataSource").getJsonObject("OBRS-DB");
        }
        String ip = obrs_db.getString("ip");
        Integer port = obrs_db.getInteger("port");
        userName = obrs_db.getString("user");
        password = obrs_db.getString("pass");
        String dbName = obrs_db.getString("dbName");
        if (ip.contains("\\"))
            url = "jdbc:sqlserver://" + ip + ";SelectMethod=cursor;DatabaseName=" + dbName;
        else
            url = "jdbc:sqlserver://" + ip + ":" + port + ";SelectMethod=cursor;DatabaseName=" + dbName;
        Exception e = getSqlConnection();
        if (e == null) {//Success! we have a connection.
            initPromise.complete();
        } else {
            initPromise.fail(e);
        }
    }

    @Override
    public void process(Message msg) {
        /*if (msg.body() instanceof String)
            return;*/
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        String customerId = tran.getString("CUSTOMER_ID");
        if (StringUtils.isNotEmpty(customerId)) {
            Exception e = getSqlConnection();
            if (e != null) {
                errorOutboundCount++;
                eb.publish(addressBook.getError(), "Connection was not establish!", addressBook.getDeliveryOptions().addHeader("error", "CONNECTION_ERROR"));
                sendException(e);
                return;
            }
            try {
                String sqlStr = "select top 1 PhoneNo from vwCustomers where CustomerId = '" + customerId + "'";
                ResultSet rs = cmd.executeQuery(sqlStr);
                if (rs.next()) {
                    String phoneNo = rs.getString("PhoneNo").trim();
                    if (StringUtils.isNotEmpty(phoneNo)) {
                        tran.put("phoneNos", correctPhoneNos(phoneNo));
                        resultOutboundCount++;
                        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions());
                    } else
                        sendError(body, "No valid phone registered in CDC for this customer-id");
                }else{
                    sendError(body, "No record found in CDC for this customer-id");
                }
            } catch (Exception ex) {
                sendException(ex);
            }
        } else {
            sendError(body, "No customer-id to search in CDC");
        }
    }

    private Exception getSqlConnection() {
        try {
            if (this.dbConnection != null) {
                if (this.dbConnection.isValid(3)) {
                    if (cmd != null && !cmd.isClosed())
                        cmd.close();
                    cmd = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                    return null;
                }
                if (!this.dbConnection.isClosed())
                    this.dbConnection.close();
            }
            this.dbConnection = DriverManager.getConnection(this.url, this.userName, this.password);
            if (cmd != null && !cmd.isClosed())
                cmd.close();
            cmd = dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            return null;
        } catch (Exception e) {
            sendException("Sql connection error!", e);
            return e;
        }
    }

    private void sendError(JsonObject body, String err) {
        errorOutboundCount++;
        eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions().addHeader("error", err));
    }
}