package com.vx6.widget.db.write;

import com.vx6.master.AddressBook;
import com.vx6.master.Buffer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqlServer extends AbstractVerticle {
    private EventBus eb;
    private boolean inputConnected = true, outputConnected = false, errorConnected = false, triggerConnected = false;
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
    private String type = "string", format = "json", delimiter = "|", headerString = "";
    private boolean header = true;
    private String query, cmd = "executeupdate";
    private JsonArray params;
    private Boolean autoNext = false;
    private int rowCount = 0;
    private LocalMap<String, Long> healthMap;
    private Integer rate = null;
    private ResultSetMetaData metadata = null;

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        healthMap = sharedData.getLocalMap(String.join(".", config().getString("graph_id"), config().getString("type"), config().getString("id")));
        this.eb = vertx.eventBus();
        addressBook = new AddressBook(config());
        if (config().containsKey("Result")) {
            outputConnected = !config().getJsonArray("Result").isEmpty();
        }
        /*if (config().containsKey("Input")) {
            inputConnected = !config().getJsonArray("Input").isEmpty();
        }*/
        if (config().containsKey("Error")) {
            errorConnected = !config().getJsonArray("Error").isEmpty();
        }
        if (config().containsKey("Trigger")) {
            triggerConnected = !config().getJsonArray("Trigger").isEmpty();
        }
        /*if (inputConnected) {*/
        bufferSize = config().getInteger("BUFFER_SIZE", 0);
        if (bufferSize > 0)
            buffer = new Buffer(eb, addressBook.getTrigger(), bufferSize);
        /*}*/
        if (!triggerConnected)
            autoNext = true;
        JsonObject config = config().getJsonObject("config");
        this.dbverticleid = config.getString("dbverticleid");
        this.query = config.getString("query");
        String ip = config.getString("ip");
        Integer port = config.getInteger("port");
        userName = config.getString("user");
        password = config.getString("pass");
        String dbName = config.getString("dbName");
        if (ip.contains("\\"))
            url = "jdbc:sqlserver://" + ip + ";SelectMethod=cursor;DatabaseName=" + dbName;
        else
            url = "jdbc:sqlserver://" + ip + ":" + port + ";SelectMethod=cursor;DatabaseName=" + dbName;
        //url = "jdbc:sqlserver://" + ip + ":" + port + ";SelectMethod=cursor;DatabaseName=" + dbName;
        Exception e = getSqlConnection();
        if (e == null) {
            /*this.eb.consumer(this.dbverticleid + ".trigger", this::trigger);*/
            this.eb.consumer(this.dbverticleid + ".input", this::input);
            startPromise.complete();
        } else {
            startPromise.fail(e);
        }
    }

    private <T> void input(Message<T> tMessage) {
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
        this.rate = body.getInteger("rate");
        switch (cmd.toLowerCase().replaceAll("[^a-z]", "")) {
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
            case "update":
                break;
            case "updateWithParams":
                break;
            case "call":
                break;
            case "callWithParams":
                break;
            default:
                //Unknown cmd!
                break;
        }
    }

    private String getHeaderString(ResultSetMetaData metadata, String delimiter) throws SQLException {
        StringBuilder header = new StringBuilder();
        for (int i = 1; i <= metadata.getColumnCount(); i++) {
            header.append(metadata.getColumnName(i)).append(delimiter);
        }
        header.deleteCharAt(header.lastIndexOf(delimiter));
        return header.toString();
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

    private String rs2str(ResultSet rs) throws SQLException {
        StringBuilder body = new StringBuilder();
        for (int i = 1; i <= this.metadata.getColumnCount(); i++) {
            body.append(rs.getString(i)).append(this.delimiter);
        }
        body.deleteCharAt(body.lastIndexOf(delimiter));
        return body.toString();
    }

    private JsonObject rs2jo(ResultSet rSet) throws SQLException {
        JsonObject jo = new JsonObject();
        int columnCount = this.metadata.getColumnCount();
        if (this.type.equalsIgnoreCase("string")) {
            for (int i = 1; i <= columnCount; i++) {
                jo.put(metadata.getColumnName(i), rSet.getString(i));
            }
        } else if (this.type.equalsIgnoreCase("generic")) {
            for (int i = 1; i <= columnCount; i++) {
                switch (metadata.getColumnTypeName(i)) {
                    case "int":
                        jo.put(metadata.getColumnName(i), rSet.getInt(i));
                        break;
                    case "bigint":
                        jo.put(metadata.getColumnName(i), rSet.getLong(i));
                        break;
                    case "bit":
                        jo.put(metadata.getColumnName(i), rSet.getBoolean(i));
                        break;
                    case "date":
                        try {
                            jo.put(metadata.getColumnName(i), rSet.getDate(i) != null ? rSet.getDate(i).toString() : null);
                        } catch (Exception e) {
                            System.out.println(metadata.getColumnName(i) + " : " + rSet.getString(i));
                        }
                        break;
                    case "datetime":
                        jo.put(metadata.getColumnName(i), rSet.getTimestamp(i) != null ? rSet.getTimestamp(i).toString() : null);
                        break;
                    case "varchar":
                    default:
                        jo.put(metadata.getColumnName(i), rSet.getString(i));
                }
            }
        }
        return jo;
    }

    private Exception getSqlConnection() {
        try {
            /*if (!(this.dbConnection == null || this.dbConnection.isValid(3)))
                this.dbConnection.close();*/
            if (this.dbConnection != null) {
                if (this.dbConnection.isValid(3))
                    return null;
                if (!this.dbConnection.isClosed())
                    this.dbConnection.close();
            }
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
