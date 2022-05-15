package com.vx6.widget.db.read;

import com.vx6.master.AddressBook;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.sql.DriverManager;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SqlServer extends AbstractVerticle {
    private EventBus eb;
    private boolean triggerConnected = false;
    private Connection dbConnection = null;
    private Statement st = null;
    private ResultSet rs;
    private String userName;
    private String password;
    private String url;
    private AddressBook addressBook;
    private String dbverticleid;
    private String type = "string", format = "json", delimiter = "|", headerString = "";
    private boolean header = true;
    private String query, cmd = "executequery";
    private Boolean autoNext = false;
    private int rowCount = 0;
    private LocalMap<String, Long> healthMap;
    private ResultSetMetaData metadata = null;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        SharedData sharedData = vertx.sharedData();
        healthMap = sharedData.getLocalMap(String.join(".", config().getString("graph_id"), config().getString("type"), config().getString("id")));
        this.eb = vertx.eventBus();
        addressBook = new AddressBook(config());
        if (config().containsKey("Trigger")) {
            triggerConnected = !config().getJsonArray("Trigger").isEmpty();
        }
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
        if (e == null) {//Success! we have a connection.
            this.eb.consumer(this.dbverticleid + ".trigger", this::trigger);
            startPromise.complete();
        } else {
            startPromise.fail(e);
        }
    }

    private <T> void trigger(Message<T> tMessage) {
        if (tMessage.body().toString().equalsIgnoreCase("ready")) {
            if (!(triggerConnected || this.query.isEmpty())) {
                autoNext = true;
                executeQuery();
            }
        } else {
            JsonObject body;
            try {
                body = new JsonObject(tMessage.body().toString());
            } catch (Exception e) {
                sendException(e);
                return;
            }
            String cmd = body.getString("cmd", this.cmd);
            this.query = body.getString("query", this.query);
            this.autoNext = body.getBoolean("autoNext", this.autoNext);

            if ("next".equalsIgnoreCase(cmd)) {
                try {
                    if (this.rs == null || this.rs.isClosed())
                        executeQuery();
                    else
                        next();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else { //execute query
                rowCount = 0;
                this.format = body.getString("format", "json");
                this.type = body.getString("type", "string");
                this.header = body.getBoolean("header", true);
                this.delimiter = body.getString("delimiter", this.delimiter);
                executeQuery();
            }
        }
    }

    private void executeQuery() {
        if (getSqlConnection() != null) {
            eb.publish(addressBook.getError(), "Connection was not establish!", addressBook.getDeliveryOptions().addHeader("error", "CONNECTION_ERROR"));
            addErrorOutboundCount();
            return;
        }
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
                LocalDateTime now = LocalDateTime.now();
                eb.publish(addressBook.getError(), "Result set received on " + dtf.format(now), addressBook.getDeliveryOptions().addHeader("type", "info"));
                addErrorOutboundCount();
                try {
                    this.metadata = this.rs.getMetaData();
                    if (this.format.equalsIgnoreCase("csv") && this.header) {
                        this.headerString = getHeaderString(this.metadata, this.delimiter);
                    }
                    next();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                sendException(new Exception(res.cause()));
            }
        });
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

    private void next() {
        try {
            if (rs.next()) {
                if (format.equalsIgnoreCase("json")) {
                    JsonObject jo = rs2jo(this.rs);
                    this.eb.publish(addressBook.getResult(), jo, addressBook.getDeliveryOptions().addHeader("rowCount", ++rowCount + ""));
                } else if (format.equalsIgnoreCase("csv")) {
                    String str = rs2str(this.rs);
                    DeliveryOptions dO = addressBook.getDeliveryOptions().addHeader("rowCount", ++rowCount + "");
                    if (this.header)
                        dO.addHeader("header", this.headerString);
                    this.eb.publish(addressBook.getResult(), str, dO);
                }
                addResultOutboundCount();
                if (autoNext)
                    this.eb.send(addressBook.getTrigger(), new JsonObject().put("cmd", "next"));
            } else {
                LocalDateTime now = LocalDateTime.now();
                this.eb.publish(addressBook.getError(), new JsonObject().put("msg", "result-set-end")
                        .put("rowCount", rowCount).put("time", dtf.format(now)), addressBook.getDeliveryOptions().addHeader("msg", "end"));
                addErrorOutboundCount();
                if (rs != null)
                    this.rs.close();
                if (st != null)
                    this.st.close();
                rs = null;
                st = null;
            }
        } catch (Exception e) {
            sendException(e);
        }
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
