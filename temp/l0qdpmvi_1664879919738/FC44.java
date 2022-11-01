import BankIranWS.BankIranWS_NOR.*;
import Utils.PersianCalendarBean;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static Utils.Library.unRevSign;

public class FC44 extends MasterVerticle {
    private static Integer seqNo = 0;
    private WsAcctStmtMessageSetPortType wsAcctStmtMessageSetPortType;
    private JsonObject bankIranWS, constants;
    private String branchNo, workstationId, terminalId, userId, stmtCode;
    private int defaultPrice = 0;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        constants = config().getJsonObject("dataSource").getJsonObject("Config");
        try {
            if (!(config().containsKey("dataSource") &&
                    config().getJsonObject("dataSource").containsKey("BankIranWS") &&
                    config().getJsonObject("dataSource").containsKey("Config")))
                initPromise.fail("BankIranWS data not found on datasource");
            else {
                bankIranWS = config().getJsonObject("dataSource").getJsonObject("BankIranWS");
                wsAcctStmtMessageSetPortType = new WsAcctStmtService(new URL(bankIranWS.getString("url"))).getWsAcctStmtPort();
                branchNo = bankIranWS.getString("branchNo");
                workstationId = bankIranWS.getString("workstationId");
                terminalId = bankIranWS.getString("terminalId");
                userId = bankIranWS.getString("userId");
                stmtCode = bankIranWS.getString("stmtCode");
                constants = config().getJsonObject("dataSource").getJsonObject("Config");
                defaultPrice = constants.getJsonObject("creditItem").getJsonObject(constants.getString("startupCreditItem")).getInteger("price");
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
        JsonObject body = (JsonObject) tMessage.body();
        String accountNo = body.getJsonObject("accountInfo").getString("AccountNo");
        String curPDate = PersianCalendarBean.getPersianDateSimple(new Date()).substring(2, 8);
        String curTime = new SimpleDateFormat("HHmmss").format(new Date());
        Long amount = body.containsKey("amount") ? body.getLong("amount") : defaultPrice;
        String curSeqNo;
        synchronized (seqNo) {
            seqNo++;
            if (seqNo > 9999) seqNo = 0;
            curSeqNo = String.format("%04d", seqNo);
        }

        FC44MSGIN fc44MSGIN = new FC44MSGIN();
        fc44MSGIN.setFCTRANID("DC44");
        fc44MSGIN.setFCCTRLID(branchNo);
        fc44MSGIN.setFCCTRID("");//new
        fc44MSGIN.setFCBRANCHNO(branchNo);
        fc44MSGIN.setFCWSID(workstationId);
        fc44MSGIN.setFCLTERM(terminalId);
        fc44MSGIN.setFCUSERID(userId);
        fc44MSGIN.setFCSEQNO(curSeqNo);
        fc44MSGIN.setFCDATE(curPDate);
        fc44MSGIN.setFCTIME(curTime);
        fc44MSGIN.setFCMODEFLAG("O");
        fc44MSGIN.setFCSTATUSFLAG("N");
        fc44MSGIN.setFCOVRHFLAG("03");
        fc44MSGIN.setFCAUTHOFFICER(userId);
        fc44MSGIN.setFCTRANLINK("001");
        fc44MSGIN.setACCOUNTNO(accountNo);
        fc44MSGIN.setTRANAMT(amount);
        fc44MSGIN.setTRANKDEQ(amount);
        fc44MSGIN.setSTATDESC(stmtCode);
        fc44MSGIN.setCHQNO("0");
        fc44MSGIN.setVALUEDATE(curPDate);
        fc44MSGIN.setOPTINFO1(" ");
        fc44MSGIN.setOPTINFO(" ");
        fc44MSGIN.setLANGCODE("F");
        fc44MSGIN.setPRINTIND("N");
        fc44MSGIN.setORIGKEY(" ");
        fc44MSGIN.setCHARGECODE("K");
        fc44MSGIN.setCHQDATE(" ");
        fc44MSGIN.setFCNATIONALID(" ");
        fc44MSGIN.setFCFXRATE(new BigDecimal("0"));
        fc44MSGIN.setFCSALERT(new BigDecimal("0"));
        fc44MSGIN.setFCORIGINOFMONEY("");
        fc44MSGIN.setFCPHASE("");
        body.put("amount", amount);
        vertx.executeBlocking(promise -> {
            try {
                FC44MSGOUT fc44MSGOUT = wsAcctStmtMessageSetPortType.fc44Opr1(fc44MSGIN);
                promise.complete(fc44MSGOUT);
            } catch (FaultMsg e) {
                deductionFailed(body, e);
                eb.publish(addressBook.getError(), body.put("error", "##### BankIranWS_NOR.Deposit accountNo Fail " + accountNo + " FC44MSGOUT general Exception"), addressBook.getDeliveryOptions().addHeader("error", "FC44").addHeader("status", "ERROR"));
                errorOutboundCount++;
                //sendException(e);
                e.printStackTrace();
                promise.fail(e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
                FC44MSGOUT fc44MSGOUT = (FC44MSGOUT) res.result();
                if (fc44MSGOUT.getALERTCODE().trim().isEmpty()) {
                    deductionFailed(body, "FC44MSGOUT_EMPTY_ALERT_CODE");
                    eb.publish(addressBook.getError(), body.put("error", "BankIranWS_NOR.Deposit Fail " + accountNo + " Invalid Empty AlertCode " + fc44MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "FC44").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else if (Integer.parseInt(fc44MSGOUT.getALERTCODE()) >= Integer.parseInt(fc44MSGIN.getFCOVRHFLAG())) {
                    deductionFailed(body, fc44MSGOUT.getALERTMSG());
                    eb.publish(addressBook.getError(), body.put("error", "##### BankIranWS_NOR.Deposit Fail " + accountNo + " " + fc44MSGOUT.getALERTMSG() + " " + fc44MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "FC44").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else {
                    JsonObject fc44out = new JsonObject();
                    //fc44out.put("remain", Long.parseLong(unRevSign(fc44MSGOUT.getAVALBAL().trim())) - amount);
                    fc44out.put("remain", Long.parseLong(unRevSign(fc44MSGOUT.getAVALBAL().trim())));
                    fc44out.put("ORIG_KEY", fc44MSGOUT.getORIGKEY());

                /*System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output Fill0          =>" + fc44MSGOUT.getFill0() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTCICSTRANSID=>" + fc44MSGOUT.getHOSTCICSTRANSID() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTSEQNO      =>" + fc44MSGOUT.getHOSTSEQNO() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTDEST       =>" + fc44MSGOUT.getHOSTDEST() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTDATATYPE   =>" + fc44MSGOUT.getHOSTDATATYPE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTDATACHAIN  =>" + fc44MSGOUT.getHOSTDATACHAIN() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTACKNOWLEDGE=>" + fc44MSGOUT.getHOSTACKNOWLEDGE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTMSGNBR     =>" + fc44MSGOUT.getHOSTMSGNBR() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output HOSTEMPTYSEND  =>" + fc44MSGOUT.getHOSTEMPTYSEND() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output FLGGLCD        =>" + fc44MSGOUT.getFLGGLCD() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output Fill1          =>" + fc44MSGOUT.getFill1() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output CUSTNAME1      =>" + fc44MSGOUT.getCUSTNAME1() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output CUSTNAME2      =>" + fc44MSGOUT.getCUSTNAME2() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output CURRBAL        =>" + fc44MSGOUT.getCURRBAL() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output AVALBAL        =>" + fc44MSGOUT.getAVALBAL() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output SHORTNAME      =>" + fc44MSGOUT.getSHORTNAME() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output ACCTCONDITIONS =>" + fc44MSGOUT.getACCTCONDITIONS() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output RESPOFFICER    =>" + fc44MSGOUT.getRESPOFFICER() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output ALERTCODE      =>" + fc44MSGOUT.getALERTCODE() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output ALERTMSG       =>" + fc44MSGOUT.getALERTMSG() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM1   =>" + fc44MSGOUT.getOFFICERCOMM1() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM2   =>" + fc44MSGOUT.getOFFICERCOMM2() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM3   =>" + fc44MSGOUT.getOFFICERCOMM3() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM4   =>" + fc44MSGOUT.getOFFICERCOMM4() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM5   =>" + fc44MSGOUT.getOFFICERCOMM5() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output OFFICERCOMM6   =>" + fc44MSGOUT.getOFFICERCOMM6() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output ORIGKEY        =>" + fc44MSGOUT.getORIGKEY() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output CHARGEAMT44    =>" + fc44MSGOUT.getCHARGEAMT44() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output PSBKFLAG       =>" + fc44MSGOUT.getPSBKFLAG() + "<");
                System.out.println("##### " + accountNo + " BankIranWS_NOR.deposit output CICSXT         =>" + fc44MSGOUT.getCICSXT() + "<");*/

                    eb.publish(addressBook.getResult(), body.put("FC44MSGOUT", fc44out), addressBook.getDeliveryOptions().addHeader("status", "SUCCESS"));
                    resultOutboundCount++;
                }
            }
        });
    }

    private void deductionFailed(JsonObject jo, FaultMsg e) {
        deductionFailed(jo, e.getFaultInfo().getFaultstring().getValue());
    }

    private void deductionFailed(JsonObject jo, String faultMsg) {
        jo.put("faultMsg", faultMsg);
    }
}