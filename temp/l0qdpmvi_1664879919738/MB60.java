import BankIranWS.BankIranWS_NOR.*;
import Utils.PersianCalendarBean;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

//سرویس دریافت اطلاعات حساب
public class MB60 extends MasterVerticle {
    private static Integer seqNo = 0;
    private WsAcctStmtMessageSetPortType wsAcctStmtMessageSetPortType;
    private JsonObject bankIranWS;
    private String branchNo, workstationId, terminalId, userId;
    private int parallelConnections = 0;
    private ArrayList<JsonObject> buf = new ArrayList<>();
    private static String label = "<h3 style=\"margin: 0;\">MB60</h3>" +
            "<h3 style=\"display: inline-block; margin: 0;\">CONs.:&nbsp;</h3>" +
            "<h2 style=\"display: inline-block; margin: 0;color: blue\">%s</h2>" +
            "<div><h2 style=\"display: inline-block; margin: 0;\">BUF.:&nbsp;</h2>" +
            "<h2 style=\"display: inline-block; margin: 0;color: orange\">%s</h2></div>";

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        eb.consumer("vx.mx", msg -> {
            try {
                var body = (JsonObject) msg.body();
                if (!Objects.equals(body.getString("type"), "sync"))
                    return;
                var time = body.getString("time").substring(14, 16) + ":" + body.getString("time").substring(17);
                UpdateLabel(time);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        });
        try {
            if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("BankIranWS")))
                initPromise.fail("BankIranWS data not found on datasource");
            else {
                bankIranWS = config().getJsonObject("dataSource").getJsonObject("BankIranWS");
                wsAcctStmtMessageSetPortType = new WsAcctStmtService(new URL(bankIranWS.getString("url"))).getWsAcctStmtPort();
                branchNo = bankIranWS.getString("branchNo");
                workstationId = bankIranWS.getString("workstationId");
                terminalId = bankIranWS.getString("terminalId");
                userId = bankIranWS.getString("userId");
                initPromise.complete();
            }
        } catch (Exception e) {
            initPromise.fail("!!!!! Error : BankIranWS_NOR  getWsAcctStmtPort() Problem=" + e.getMessage());
            sendException(e);
        }
    }

    @Override
    public <T> void process(Message<T> tMessage) {
        resultOutboundCount--;
        if (tMessage.body() instanceof JsonObject) {
            JsonObject jo = (JsonObject) tMessage.body();
            if (parallelConnections < setting.getInteger("parallelConnections", 1)) {
                process(jo);
            } else {
                buf.add(jo);
            }
        }
    }

    private void process(JsonObject jo) {
        String accountNo = jo.getJsonObject("accountInfo").getString("AccountNo");
        String curPDate = PersianCalendarBean.getPersianDateSimple(new Date()).substring(2, 8);
        String curTime = new SimpleDateFormat("HHmmss").format(new Date());
        String curSeqNo;
        synchronized (seqNo) {
            seqNo++;
            if (seqNo > 9999) seqNo = 0;
            curSeqNo = String.format("%04d", seqNo);
        }

        MB60MSGIN mb60MSGIN = new MB60MSGIN();
        mb60MSGIN.setFCTRANID("MB60");
        mb60MSGIN.setFCCTRLID(branchNo);
        mb60MSGIN.setFCBRANCHNO(branchNo);
        mb60MSGIN.setFCWSID(workstationId);
        mb60MSGIN.setFCLTERM(terminalId);
        mb60MSGIN.setFCUSERID(userId);
        mb60MSGIN.setFCSEQNO(curSeqNo);
        mb60MSGIN.setFCDATE(curPDate);
        mb60MSGIN.setFCTIME(curTime);
        mb60MSGIN.setFCMODEFLAG("O");
        mb60MSGIN.setFCSTATUSFLAG("N");
        mb60MSGIN.setFCOVRHFLAG("01");
        mb60MSGIN.setFCAUTHOFFICER(userId);
        mb60MSGIN.setFCTRANLINK("001");
        mb60MSGIN.setACNO(accountNo);
        mb60MSGIN.setACCCTRLFLG("0");
        parallelConnections++;
        vertx.executeBlocking(promise -> {
            if (health.getInteger("parallelConnections", 0) < parallelConnections)
                health.put("parallelConnections", parallelConnections);

            try {
                MB60MSGOUT mb60MSGOUT = wsAcctStmtMessageSetPortType.mb60Opr1(mb60MSGIN);
                promise.complete(mb60MSGOUT);
            } catch (FaultMsg e) {
                deductionFailed(jo, e);
                eb.publish(addressBook.getError(), jo.put("error", "##### BankIranWS_NOR.AccountInfo AccountNo Fail " + accountNo + " MB60MSGOUT general Exception"), addressBook.getDeliveryOptions().addHeader("error", "MB60").addHeader("status", "ERROR"));
                errorOutboundCount++;
                //sendException(e);
                e.printStackTrace();
                promise.fail(e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
                MB60MSGOUT mb60MSGOUT = (MB60MSGOUT) res.result();
                if (mb60MSGOUT.getALERTCODE().trim().isEmpty()) {
                    deductionFailed(jo, "MB60MSGOUT_EMPTY_ALERT_CODE");
                    eb.publish(addressBook.getError(), jo.put("error", "##### BankIranWS_NOR.AccountInfo AccountNo Fail " + accountNo + " Invalid Empty AlertCode " + mb60MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "MB60").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else if (Integer.parseInt(mb60MSGOUT.getALERTCODE()) >= Integer.parseInt(mb60MSGIN.getFCOVRHFLAG())) {
                    deductionFailed(jo, mb60MSGOUT.getALERTMSGE());
                    eb.publish(addressBook.getError(), jo.put("error", "##### BankIranWS_NOR.AccountInfo AccountNo Fail " + accountNo + " " + mb60MSGOUT.getALERTMSGE() + " " + mb60MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "MB60").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else {
                    JsonObject mb60out = new JsonObject();
                    mb60out.put("Fill0", mb60MSGOUT.getFill0())
                            .put("HOSTCICSTRANSID", mb60MSGOUT.getHOSTCICSTRANSID())
                            .put("HOSTSEQNO", mb60MSGOUT.getHOSTSEQNO())
                            .put("HOSTDEST", mb60MSGOUT.getHOSTDEST())
                            .put("HOSTDATATYPE", mb60MSGOUT.getHOSTDATATYPE())
                            .put("HOSTDATACHAIN", mb60MSGOUT.getHOSTDATACHAIN())
                            .put("HOSTACKNOWLEDGE", mb60MSGOUT.getHOSTACKNOWLEDGE())
                            .put("HOSTMSGNBR", mb60MSGOUT.getHOSTMSGNBR())
                            .put("HOSTEMPTYSEND", mb60MSGOUT.getHOSTEMPTYSEND())
                            .put("FLGGLCD", mb60MSGOUT.getFLGGLCD())
                            .put("Fill1", mb60MSGOUT.getFill1())
                            .put("ALERTCODE", mb60MSGOUT.getALERTCODE())
                            .put("BRNCHNO", mb60MSGOUT.getBRNCHNO())
                            .put("ACCOUNTSTATCODE", mb60MSGOUT.getACCOUNTSTATCODE())
                            .put("ACCOUNTSTATDESC", mb60MSGOUT.getACCOUNTSTATDESC())
                            .put("ACCOUNTSTYPCODE", mb60MSGOUT.getACCOUNTSTYPCODE())
                            .put("ACCOUNTSTYPDESC", mb60MSGOUT.getACCOUNTSTYPDESC())
                            .put("CUSTTYPE", mb60MSGOUT.getCUSTTYPE())
                            .put("IDNUMBER", mb60MSGOUT.getIDNUMBER())
                            .put("CUSTFNAME", mb60MSGOUT.getCUSTFNAME())
                            .put("CUSTLNAME", mb60MSGOUT.getCUSTLNAME())
                            .put("SBOPENRES", mb60MSGOUT.getSBOPENRES())
                            .put("CICSXT", mb60MSGOUT.getCICSXT())
                            .put("ALERTMSGE", mb60MSGOUT.getALERTMSGE())
                            .put("SBCUSNO", mb60MSGOUT.getSBCUSNO())
                            .put("CUSTSUBTYP", mb60MSGOUT.getCUSTSUBTYP())
                            .put("accountCurrency", accountNo.substring(10, 12));
                /*System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output FILL0           =>" + mb60MSGOUT.getFill0() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTCICSTRANSID =>" + mb60MSGOUT.getHOSTCICSTRANSID() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTSEQNO       =>" + mb60MSGOUT.getHOSTSEQNO() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTDEST        =>" + mb60MSGOUT.getHOSTDEST() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTDATATYPE    =>" + mb60MSGOUT.getHOSTDATATYPE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTDATACHAIN   =>" + mb60MSGOUT.getHOSTDATACHAIN() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTACKNOWLEDGE =>" + mb60MSGOUT.getHOSTACKNOWLEDGE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTMSGNBR      =>" + mb60MSGOUT.getHOSTMSGNBR() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HOSTEMPTYSEND   =>" + mb60MSGOUT.getHOSTEMPTYSEND() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output FLGGLCD         =>" + mb60MSGOUT.getFLGGLCD() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output FILL1           =>" + mb60MSGOUT.getFill1() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ALERTCODE       =>" + mb60MSGOUT.getALERTCODE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output BRNCHNO         =>" + mb60MSGOUT.getBRNCHNO() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ACCOUNTSTATCODE =>" + mb60MSGOUT.getACCOUNTSTATCODE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ACCOUNTSTATDESC =>" + mb60MSGOUT.getACCOUNTSTATDESC() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ACCOUNTSTYPCODE =>" + mb60MSGOUT.getACCOUNTSTYPCODE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ACCOUNTSTYPDESC =>" + mb60MSGOUT.getACCOUNTSTYPDESC() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output CUSTTYPE        =>" + mb60MSGOUT.getCUSTTYPE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output IDNUMBER        =>" + mb60MSGOUT.getIDNUMBER() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output CUSTFNAME       =>" + mb60MSGOUT.getCUSTFNAME() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output CUSTLNAME       =>" + mb60MSGOUT.getCUSTLNAME() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output SBOPENRES       =>" + mb60MSGOUT.getSBOPENRES() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output CICSXT          =>" + mb60MSGOUT.getCICSXT() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output ALERTMSG        =>" + mb60MSGOUT.getALERTMSGE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HTCUSNO         =>" + mb60MSGOUT.getSBCUSNO() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.AccountInfo output HTACSUBTYP      =>" + mb60MSGOUT.getCUSTSUBTYP() + "<");*/
                    eb.publish(addressBook.getResult(), jo.put("MB60MSGOUT", mb60out), addressBook.getDeliveryOptions().addHeader("status", "SUCCESS"));
                    resultOutboundCount++;
                }
            }
            parallelConnections--;
            if (parallelConnections < setting.getInteger("parallelConnections", 1) && buf.size() > 0) {
                JsonObject bjo = buf.get(0);
                buf.remove(0);
                process(bjo);
            }
        });
    }

    private void deductionFailed(JsonObject jo, FaultMsg e) {
        deductionFailed(jo, e.getFaultInfo().getFaultstring().getValue());
    }

    private void deductionFailed(JsonObject jo, String faultMsg) {
        jo.put("faultMsg", faultMsg);
    }

    private void UpdateLabel(String time) {
        sendLabel(String.format(label, parallelConnections, buf.size() + ""), "18ff96", "50", "efff18");
    }
}