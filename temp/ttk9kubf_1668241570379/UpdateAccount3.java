import com.vx6.master.MasterVerticle;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.web.common.template.impl.TemplateHolder;

import java.io.IOException;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
//import java.util.Date;

public class UpdateAccount3 extends MasterVerticle {
    private SharedData sharedData;
    private static final String Cache_Name = "sql_template_patterns";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd hh:mm:ss a");
    private StringBuilder query = new StringBuilder();
    private LocalMap<String, TemplateHolder<Template>> cache;
   /* private static final String updateQuery = "update tblAccounts3" +
            " set CustomerId = '%s', [Enable] = '%b', TransGroupsStr = '%s', Phones = '%s'," +
            " RemainAmount = %d, RemainDays = %d, DueDate = '%s'," +
            " LowLimitCredit = %d, LowLimitDebit = %d," +
            " NearExpireNotifyDate = '%s', ExpiredNotifyDate = '%s', [ExpireDate] = '%s'," +
            " [AutoRenewRegister] = '%b', [AutoRenewRegCancelNotifyDate] = '%s', EditDT = '%s'" +
            " where AccountNo = '%s'; ";
    private static final String insertQuery = "insert into tblAccounts3" +
            " (AccountNo, [Enable], CustomerId, TransGroupsStr, Phones, RemainAmount, RemainDays, DueDate," +
            " LowLimitCredit, LowLimitDebit, NearExpireNotifyDate, ExpiredNotifyDate, [ExpireDate] ," +
            " AutoRenewRegister, AutoRenewRegCancelNotifyDate, EditDT)" +
            " values ( '%s', '%b', '%s', '%s', '%s', %d, %d, '%s', %d, %d, '%s', '%s', '%s', '%b', '%s', '%s' ); ";
    private static final String historyQuery = "insert into dbo.TblAccounts3History" +
            " (AccountNo, Enable, CustomerId, TransGroupsStr, Phones, RemainAmount, RemainDays, DueDate," +
            " LowLimitCredit, LowLimitDebit, ExpireDate, NearExpireNotifyDate, ExpiredNotifyDate, EditDT," +
            " AutoRenewRegister, AutoRenewRegCancelNotifyDate, Flag)" +
            " values ('%s', '%b', '%s', '%s', '%s', %d, %d, '%s', %d, %d, '%s', '%s', '%s', '%s', '%b', '%s', %d); ";
    private static final String registerQuery = "insert into [dbo].[tblRegister3]" +
            " ([CreateDT] ,[Command] ,[AccountNo] ,[CustomerId] ,[PhoneNo] ,[ExpireDate] ,[LowLimitCredit] ," +
            " [TransactionGroupNames] ,[CreditItemName] ,[ExtraPhoneNo] ,[LanguageCode] ,[CmdDate] ,[CmdTime] ," +
            " [Branch] ,[TerminalId] ,[user])" +
            " VALUES ('%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s' ,'%s'); ";*/

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        sharedData = vertx.sharedData();
        cache = sharedData.getLocalMap(Cache_Name);
        super.initialize(initPromise);
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject newAccountInfo = body.getJsonObject("newAccountInfo");
        String sqlCmd = null;
        TemplateHolder<Template> th;
        if (body.containsKey("tran")) {
            th = cache.get("registerQuery");
            JsonObject tran = body.getJsonObject("tran");
            //Insert transaction in register table
            if (th != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    th.template().process(tran.getMap(), stringWriter);
                    sqlCmd = stringWriter.toString();
                    publishOut(0, sqlCmd);
                    query.append(sqlCmd);//.append(System.getProperty("line.separator"));
                } catch (TemplateException | IOException e) {
                    eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                    errorOutboundCount++;
                    //e.printStackTrace();
                }
            } else {
                eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
                errorOutboundCount++;
            }
            /*sqlCmd = String.format(registerQuery,
                    sdf.format(new Date()),
                    tran.getString("COMMAND"),
                    tran.getString("ACCOUNTNO"),
                    tran.getString("CUSTOMERID"),
                    tran.getString("PHONENO"),
                    tran.getString("EXPIREDATE"),
                    tran.getString("LOWLIMITCREDIT"),
                    tran.getString("TRANSACTIONGROUPNAMES"),
                    tran.getString("CREDITITEMNAME"),
                    tran.getString("EXTRAPHONENO"),
                    tran.getString("LANGUAGECODE"),
                    tran.getString("CMDDATE"),
                    tran.getString("CMDTIME"),
                    tran.getString("BRANCH"),
                    tran.getString("TERMINALID"),
                    tran.getString("USER")
            );
            publishOut(0, sqlCmd);
            query.append(sqlCmd);//.append(System.getProperty("line.separator"));*/
        }
        if (body.containsKey("accountInfo")) {
            th = cache.get("updateQuery");
            JsonObject accountInfo = body.getJsonObject("accountInfo");
            //Update account info in account table
            if (th != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    th.template().process(newAccountInfo.getMap(), stringWriter);
                    sqlCmd = stringWriter.toString();
                    publishOut(0, sqlCmd);
                    query.append(sqlCmd);//.append(System.getProperty("line.separator"));
                } catch (TemplateException | IOException e) {
                    eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                    errorOutboundCount++;
                    //e.printStackTrace();
                }
            } else {
                eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
                errorOutboundCount++;
            }
            /*sqlCmd = String.format(updateQuery,
                    newAccountInfo.getString("CustomerId"),
                    newAccountInfo.getBoolean("Enable"),
                    newAccountInfo.getString("TransGroupsStr"),
                    newAccountInfo.getString("Phones"),
                    Long.parseLong(newAccountInfo.getString("RemainAmount")),
                    Long.parseLong(newAccountInfo.getString("RemainDays")),
                    newAccountInfo.getString("DueDate"),
                    Long.parseLong(newAccountInfo.getString("LowLimitCredit")),
                    Long.parseLong(newAccountInfo.getString("LowLimitDebit")),
                    newAccountInfo.getString("NearExpireNotifyDate"),
                    newAccountInfo.getString("ExpiredNotifyDate"),
                    newAccountInfo.getString("ExpireDate"),
                    newAccountInfo.getBoolean("AutoRenewRegister"),
                    newAccountInfo.getString("AutoRenewRegCancelNotifyDate"),
                    *//*newAccountInfo.getString("EditDT"),*//*
                    new Date().toInstant(),
                    newAccountInfo.getString("AccountNo")
            );
            publishOut(0, sqlCmd);
            query.append(sqlCmd);//.append(System.getProperty("line.separator"));*/
            th = cache.get("historyQuery");
            //Insert previous account info in history table
            if (th != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    th.template().process(accountInfo.getMap(), stringWriter);
                    sqlCmd = stringWriter.toString();
                    publishOut(0, sqlCmd);
                    query.append(sqlCmd);//.append(System.getProperty("line.separator"));
                } catch (TemplateException | IOException e) {
                    eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                    errorOutboundCount++;
                    //e.printStackTrace();
                }
            } else {
                //eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
                //errorOutboundCount++;
            }
            /*sqlCmd = String.format(historyQuery,
                    accountInfo.getString("AccountNo"),
                    accountInfo.getBoolean("Enable"),
                    accountInfo.getString("CustomerId"),
                    accountInfo.getString("TransGroupsStr"),
                    accountInfo.getString("Phones"),
                    Long.parseLong(accountInfo.getString("RemainAmount")),
                    Long.parseLong(accountInfo.getString("RemainDays")),
                    accountInfo.getString("DueDate"),
                    Long.parseLong(accountInfo.getString("LowLimitCredit")),
                    Long.parseLong(accountInfo.getString("LowLimitDebit")),
                    accountInfo.getString("ExpireDate"),
                    accountInfo.getString("NearExpireNotifyDate"),
                    accountInfo.getString("ExpiredNotifyDate"),
                    accountInfo.getString("EditDT"),
                    accountInfo.getBoolean("AutoRenewRegister"),
                    accountInfo.getString("AutoRenewRegCancelNotifyDate"),
                    accountInfo.getString("Flag") == null ? null : Integer.parseInt(accountInfo.getString("Flag"))
            );
            publishOut(0, sqlCmd);
            query.append(sqlCmd);*/
        } else {
            th = cache.get("insertQuery");
            //Insert account info in account table
            if (th != null) {
                StringWriter stringWriter = new StringWriter();
                try {
                    th.template().process(newAccountInfo.getMap(), stringWriter);
                    sqlCmd = stringWriter.toString();
                    publishOut(0, sqlCmd);
                    query.append(sqlCmd);
                } catch (TemplateException | IOException e) {
                    eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", e.getMessage()));
                    errorOutboundCount++;
                    //e.printStackTrace();
                }
            } else {
                eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg).addHeader("cause", "template_not_found"));
                errorOutboundCount++;
            }
            /*sqlCmd = String.format(insertQuery,
                    newAccountInfo.getString("AccountNo"),
                    newAccountInfo.getBoolean("Enable"),
                    newAccountInfo.getString("CustomerId"),
                    newAccountInfo.getString("TransGroupsStr"),
                    newAccountInfo.getString("Phones"),
                    Long.parseLong(newAccountInfo.getString("RemainAmount")),
                    Long.parseLong(newAccountInfo.getString("RemainDays")),
                    newAccountInfo.getString("DueDate"),
                    Long.parseLong(newAccountInfo.getString("LowLimitCredit")),
                    Long.parseLong(newAccountInfo.getString("LowLimitDebit")),
                    newAccountInfo.getString("NearExpireNotifyDate"),
                    newAccountInfo.getString("ExpiredNotifyDate"),
                    newAccountInfo.getString("ExpireDate"),
                    newAccountInfo.getBoolean("AutoRenewRegister"),
                    newAccountInfo.getString("AutoRenewRegCancelNotifyDate"),
                    newAccountInfo.getString("EditDT"),
                    new Date().toInstant()
            );*/
        }
    }

    @Override
    public <T> void noCmd(Message<T> tMessage, String cmd) {
        if (query.length() > 0) {
            eb.publish(addressBook.getResult(), new JsonObject().put("query", this.query.toString().replaceAll("'null'", "''")).put("cmd", "executeUpdate"),
                    addressBook.getDeliveryOptions().addHeader("cmd", "executeUpdate")
                        /*.addHeader("count", msg.headers().get("count")).addHeader("key", msg.headers().get("key"))
                        .addHeader("channel", msg.headers().get("channel"))*/);
            query.setLength(0);
            resultOutboundCount++;
        }
    }
}