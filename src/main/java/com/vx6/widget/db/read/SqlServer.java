package com.vx6.widget.db.read;

import com.vx6.master.AddressBook;
import com.vx6.master.Buffer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqlServer extends AbstractVerticle {
    private EventBus eb;
    private JsonObject setting = new JsonObject();
    private boolean inputConnected = false, outputConnected = false, errorConnected = false, triggerConnected = false;
    private int bufferSize = 0;
    private Buffer buffer = null;
    private JsonObject clientConfig;
    private Connection dbConnection = null;
    private Statement st = null;
    private ResultSet rs;
    private String userName;
    private String password;
    private String url;
    private AddressBook addressBook;
    private String dbverticleid;
    private String queryName = "query", paramsName = "params", cmdName = "cmd", resultName = "result";
    private String query, cmd;
    private JsonArray params;
    private Boolean autoNext = false;
    private int rowCount = 0;
    private LocalMap<String, Long> healthMap;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        this.eb = vertx.eventBus();
        setting = config().getJsonObject("data").getJsonObject("setting");
        this.dbverticleid = config().getString("dbverticleid");
        addressBook = new AddressBook(config());
        if (config().containsKey("Result")) {
            outputConnected = !config().getJsonArray("Result").isEmpty();
        }
        if (config().containsKey("Input")) {
            inputConnected = !config().getJsonArray("Input").isEmpty();
        }
        if (config().containsKey("Error")) {
            errorConnected = !config().getJsonArray("Error").isEmpty();
        }
        if (config().containsKey("Trigger")) {
            triggerConnected = !config().getJsonArray("Trigger").isEmpty();
        }
        if (inputConnected) {
            bufferSize = config().getJsonObject("data").getJsonObject("config").getInteger("BUFFER_SIZE", 0);
            if (bufferSize > 0)
                buffer = new Buffer(eb, addressBook.getTrigger(), bufferSize);
        }
        if (!triggerConnected)
            autoNext = true;
        JsonObject data = config().getJsonObject("data");
        JsonObject config = data.getJsonObject("config");
        JsonObject setting = data.getJsonObject("setting");
        this.query = setting.getString("query");
        String ip = config.getString("ip");
        Integer port = config.getInteger("port");
        userName = config.getString("user");
        password = config.getString("pass");
        String dbName = config.getString("dbName");
        url = "jdbc:sqlserver://" + ip + ":" + port + ";SelectMethod=cursor;DatabaseName=" + dbName;
        Exception e = getSqlConnection();
        healthMap = sharedData.getLocalMap(String.join(".", config().getString("graph_id"), config().getString("type"), config().getString("id")));
        if (e == null) {
            this.eb.consumer(this.dbverticleid + ".trigger", this::trigger);
            this.eb.consumer(this.dbverticleid + ".input", this::input);
            startPromise.complete();
        } else {
            startPromise.fail(e);
        }
    }

    private <T> void input(Message<T> tMessage) {
    }

    private <T> void trigger(Message<T> tMessage) {
        String cmdName = tMessage.headers().get("cmdName") == null ? this.cmdName : tMessage.headers().get("cmdName");
        String queryName = tMessage.headers().get("queryName") == null ? this.queryName : tMessage.headers().get("queryName");
        String resultName = tMessage.headers().get("resultName") == null ? this.resultName : tMessage.headers().get("resultName");
        String paramsName = tMessage.headers().get("paramsName") == null ? this.paramsName : tMessage.headers().get("paramsName");
        JsonObject body;
        try {
            body = new JsonObject(tMessage.body().toString());
        } catch (Exception e) {
            sendException(e);
            return;
        }
        String cmd = body.getString(cmdName, this.cmd);
        String query = body.getString(queryName, this.query);
        JsonArray params = body.getJsonArray(paramsName, this.params);
        this.autoNext = body.getBoolean("autoNext", this.autoNext);
        switch (cmd.toLowerCase().replaceAll("[^a-z]", "")) {
            case "executequery":
                rowCount = 0;
                if (getSqlConnection() != null)
                    break;
                vertx.executeBlocking(promise -> {
                    try {
                        if (!(st == null || st.isClosed()))
                            st.close();
                        st = this.dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                        promise.complete(this.st.executeQuery(query));
                    } catch (SQLException e) {
                        sendException(e);
                        promise.fail(e);
                    }
                }, res -> {
                    if (res.succeeded()) {
                        this.rs = (ResultSet) res.result();
                        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                        LocalDateTime now = LocalDateTime.now();
                        eb.publish(addressBook.getError(), "Result set received on " + dtf.format(now) , addressBook.getDeliveryOptions().addHeader("type", "info"));
                        addErrorOutboundCount();
                        next();
                    }else{
                        eb.publish(addressBook.getError(), res.cause().getStackTrace(), addressBook.getDeliveryOptions().addHeader("type", "error"));
                        addErrorOutboundCount();
                    }
                });
                break;
            case "executeupdate":
                if (getSqlConnection() != null)
                    break;
                Instant start = Instant.now();
                vertx.executeBlocking(promise -> {
                    try (Statement st = this.dbConnection.createStatement()) {
                        int rs = st.executeUpdate(query);
                        promise.complete(rs);
                    } catch (SQLException e) {
                        sendException(e);
                        promise.fail(e);
                    }
                    /*try {
                        Statement st = this.dbConnection.createStatement();
                        int rs = st.executeUpdate(query);
                        promise.complete(rs);
                    } catch (Exception e) {
                        sendException(e);
                        promise.fail(e);
                    }*/
                }, res -> {
                    if (res.succeeded()) {
                        this.eb.publish(addressBook.getResult(), body.put(this.resultName, res.result()), addressBook.getDeliveryOptions(tMessage)
                                .addHeader("duration", Duration.between(start, Instant.now()).toMillis() + ""));
                        addResultOutboundCount();
                    } else {
                        this.eb.publish(addressBook.getError(), body.put(this.resultName, res.result()),
                                addressBook.getDeliveryOptions(tMessage)
                                        .addHeader("result", "error")
                                        .addHeader("duration", Duration.between(start, Instant.now()).toMillis() + ""));
                        addErrorOutboundCount();
                    }
                });
                break;
            case "queryWithParams":
                rowCount = 0;
                break;
            case "querySingle":
                rowCount = 0;
                break;
            case "querySingleWithParams":
                rowCount = 0;
                break;
            case "update":
                break;
            case "updateWithParams":
                break;
            case "call":
                break;
            case "callWithParams":
                break;
            case "next":
                next();
                break;
            default:
                //Unknown cmd!
                break;
        }
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if (this.rs != null) {
            this.rs.close();
            this.rs = null;
        }
        if (this.dbConnection != null) {
            this.dbConnection.close();
            this.dbConnection = null;
        }
        stopPromise.complete();
    }

    private void next() {
        try {
            if (!this.rs.isClosed() && this.rs.next()) {
                JsonObject jo = rs2jo(this.rs);
                this.eb.publish(addressBook.getResult(), jo, addressBook.getDeliveryOptions().addHeader("rowCount", ++rowCount + ""));
                addResultOutboundCount();
                if (autoNext)
                    /*this.eb.publish(this.dbverticleid + ".trigger", new JsonObject().put("cmd", "next"));*/
                    this.eb.send(addressBook.getTrigger(), new JsonObject().put("cmd", "next"));
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                this.eb.publish(addressBook.getError(), new JsonObject().put("msg", "result-set-end")
                        .put("rowCount", rowCount).put("time", dtf.format(now)), addressBook.getDeliveryOptions().addHeader("msg", "end"));
                addErrorOutboundCount();
                this.rs.close();
                this.st.close();
            }
        } catch (Exception e) {
            sendException(e);
        }
    }

    private JsonObject rs2jo(ResultSet rset) throws SQLException {
        JsonObject jo = new JsonObject();
        ResultSetMetaData metadata = rset.getMetaData();
        int columnCount = metadata.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            jo.put(metadata.getColumnName(i), rset.getString(i));
        }
        return jo;
    }

    private Exception getSqlConnection() {
        try {
            if (!(this.dbConnection == null || this.dbConnection.isValid(3)))
                this.dbConnection.close();
            this.dbConnection = DriverManager.getConnection(this.url, this.userName, this.password);
            return null;
        } catch (Exception e) {
            sendException("Sql connection error!", e);
            return e;
        }
    }

    private void sendException(String s, Exception e) {
        eb.publish(addressBook.getError(), s, addressBook.getDeliveryOptions().addHeader("type", "error").addHeader("stack-trace", ExceptionUtils.getStackTrace(e)));
        addErrorOutboundCount();
    }

    private void sendException(Exception e) {
        eb.publish(addressBook.getError(), ExceptionUtils.getStackTrace(e), addressBook.getDeliveryOptions().addHeader("type", "error"));
        addErrorOutboundCount();
    }

    private void addErrorOutboundCount() {
        healthMap.put("errorOutboundCount", healthMap.get("errorOutboundCount") + 1);
    }

    private void addResultOutboundCount() {
        healthMap.put("resultOutboundCount", healthMap.get("resultOutboundCount") + 1);
    }
}
