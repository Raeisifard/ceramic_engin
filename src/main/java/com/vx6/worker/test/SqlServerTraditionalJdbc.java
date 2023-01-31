package com.vx6.worker.test;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlServerTraditionalJdbc extends AbstractVerticle {
    private static final Logger log = LoggerFactory.getLogger(SqlServerTraditionalJdbc.class);

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        EventBus eb = vertx.eventBus();
        eb.consumer("mx.vx.test.sqlserver", this::processMessage);
        log.info("Starting verticle {" + this + "}");
        startPromise.complete();
    }

    private <T> void processMessage(Message<T> tMessage) {
        Connection dbConnection = null;
        JsonObject config = (JsonObject) tMessage.body();
        String ip = config.getString("ip");
        Integer port = config.getInteger("port");
        String userName = config.getString("user");
        String password = config.getString("pass");
        String dbName = config.getString("dbName");
        String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
        String url = "";
        if (ip.contains("\\"))
            url = "jdbc:sqlserver://" + ip + ";SelectMethod=cursor;DatabaseName=" + dbName;
        else
            url = "jdbc:sqlserver://" + ip + ":" + port + ";SelectMethod=cursor;DatabaseName=" + dbName;
        DeliveryOptions opt = new DeliveryOptions();
        try {
            Class.forName(driver);
            dbConnection = DriverManager.getConnection(url, userName, password);
            if (dbConnection.isValid(3)) {
                opt.addHeader("ip", ip)
                        .addHeader("port", port + "")
                        .addHeader("dbName", dbName)
                        .addHeader("dbType", "sqlServer");
                tMessage.reply("{\"testStatus\": \"ok\"}", opt);
            } else {
                tMessage.fail(0, "{\"testStatus\": \"failed\", \"cause\": \"Connection is not valid!\"}");
            }
        } catch (Exception e) {
            tMessage.fail(0, "{\"testStatus\": \"failed\", \"cause\": " + exceptionAsString(e) + "}");
        }
        if (dbConnection != null) {
            try {
                dbConnection.close();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            dbConnection = null;
        }
    }

    private String exceptionAsString(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}

