package com.vx6.widget.db.read;

import com.vx6.master.AddressBook;
import com.vx6.master.Buffer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.sql.*;
import java.sql.DriverManager;
import java.time.Duration;
import java.time.Instant;

public class SqlServer extends AbstractVerticle {
  private EventBus eb;
  private JsonObject setting = new JsonObject();
  private boolean inputConnected = false, outputConnected = false, errorConnected = false, triggerConnected = false;
  private int bufferSize = 0;
  private Buffer buffer = null;
  private JsonObject clientConfig;
  private Connection dbConnection = null;
  private ResultSet rs;
  private String userName;
  private String password;
  private String url;
  private AddressBook addressBook;
  private String ibmmqverticleid;
  private String queryName = "query", paramsName = "params", cmdName = "cmd", resultName = "result";
  private String query, cmd;
  private JsonArray params;
  private Boolean autoNext = false;
  private int rowCount = 0;

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    this.eb = vertx.eventBus();
    setting = config().getJsonObject("data").getJsonObject("setting");
    this.ibmmqverticleid = config().getString("ibmmqverticleid");
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
    if (e == null) {
      this.eb.consumer(this.ibmmqverticleid + ".trigger", this::trigger);
      this.eb.consumer(this.ibmmqverticleid + ".input", this::input);
      startPromise.complete();
    } else {
      startPromise.fail(e);
    }
  }

  private <T> void input(Message<T> tMessage) {
  }

  private <T> void trigger(Message<T> tMessage) {
    this.cmdName = tMessage.headers().get("cmdName") == null ? this.cmdName : tMessage.headers().get("cmdName");
    this.queryName = tMessage.headers().get("queryName") == null ? this.queryName : tMessage.headers().get("queryName");
    this.resultName = tMessage.headers().get("resultName") == null ? this.resultName : tMessage.headers().get("resultName");
    this.paramsName = tMessage.headers().get("paramsName") == null ? this.paramsName : tMessage.headers().get("paramsName");
    JsonObject body;
    try {
      body = new JsonObject(tMessage.body().toString());
    } catch (Exception e) {
      sendException(e);
      return;
    }
    this.cmd = body.getString(this.cmdName, this.cmd);
    this.query = body.getString(this.queryName, this.query);
    this.params = body.getJsonArray(this.paramsName, this.params);
    this.autoNext = body.getBoolean("autoNext", this.autoNext);
    switch (cmd.toLowerCase().replaceAll("[^a-z]", "")) {
      case "executequery":
        rowCount = 0;
        if (getSqlConnection() != null)
          break;
        vertx.executeBlocking(promise -> {
          try {
            Statement st = this.dbConnection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            this.rs = st.executeQuery(query);
            promise.complete(this.rs);
          } catch (Exception e) {
            sendException(e);
            promise.fail(e);
          }
        }, res -> {
          if (res.succeeded())
            next();
        });
        break;
      case "executupdate":
        if (getSqlConnection() != null)
          break;
        Instant start = Instant.now();
        vertx.executeBlocking(promise -> {
          try {
            Statement st = this.dbConnection.createStatement();
            int rs = st.executeUpdate(query);
            promise.complete(rs);
          } catch (Exception e) {
            sendException(e);
            promise.fail(e);
          }
        }, res -> {
          if (res.succeeded())
            this.eb.publish(addressBook.getResult(), body.put(this.resultName, res.result()), addressBook.getDeliveryOptions(tMessage)
              .addHeader("duration", Duration.between(start, Instant.now()).toMillis() + ""));
          else
            this.eb.publish(addressBook.getError(), body.put(this.resultName, res.result()),
              addressBook.getDeliveryOptions(tMessage)
                .addHeader("result", "error")
                .addHeader("duration", Duration.between(start, Instant.now()).toMillis() + ""));
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
      if (!this.rs.isClosed()) {
        if (this.rs.next()) {
          JsonObject jo = rs2jo(this.rs);
          this.eb.publish(addressBook.getResult(), jo, addressBook.getDeliveryOptions().addHeader("rowCount", ++rowCount + ""));
          if (autoNext)
            /*this.eb.publish(this.ibmmqverticleid + ".trigger", new JsonObject().put("cmd", "next"));*/
            this.eb.send(addressBook.getTrigger(), new JsonObject().put("cmd", "next"));
        } else {
          this.eb.publish(addressBook.getError(), new JsonObject().put("msg", "result-set-end").put("rowCount", rowCount));
          this.rs.close();
        }
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
      if (this.dbConnection == null)
        this.dbConnection = DriverManager.getConnection(this.url, this.userName, this.password);
      else if (!this.dbConnection.isValid(3)) {
        this.dbConnection.close();
        this.dbConnection = DriverManager.getConnection(this.url, this.userName, this.password);
      }
      return null;
    } catch (Exception e) {
      sendException("Sql connection error!", e);
      return e;
    }
  }

  private void sendException(String s, Exception e) {
    eb.publish(addressBook.getError(), s, addressBook.getDeliveryOptions().addHeader("type", "error").addHeader("stack-trace", ExceptionUtils.getStackTrace(e)));
  }

  private void sendException(Exception e) {
    eb.publish(addressBook.getError(), ExceptionUtils.getStackTrace(e), addressBook.getDeliveryOptions().addHeader("type", "error"));
  }
}
