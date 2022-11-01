import BankIranWS.BankIranWS_NOR.*;
import Utils.PersianCalendarBean;
import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

//سرویس دریافت اطلاعات مشتری
public class FC87 extends MasterVerticle {
    private static Integer seqNo = 0;
    private WsAcctStmtMessageSetPortType wsAcctStmtMessageSetPortType;
    private JsonObject bankIranWS;
    private String branchNo, workstationId, terminalId, userId;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        try {
            if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("BankIranWS2")))
                initPromise.fail("BankIranWS2 data not found on datasource");
            else {
                bankIranWS = config().getJsonObject("dataSource").getJsonObject("BankIranWS2");
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
        JsonObject body = (JsonObject) tMessage.body();
        String curPDate = PersianCalendarBean.getPersianDateSimple(new Date()).substring(2, 8);
        String curTime = new SimpleDateFormat("HHmmss").format(new Date());
        //String customerNo = body.getString("customerNo");
        String customerNo = body.getJsonObject("MB60MSGOUT").getString("SBCUSNO");
        String curSeqNo;
        synchronized (seqNo) {
            seqNo++;
            if (seqNo > 9999) seqNo = 0;
            curSeqNo = String.format("%04d", seqNo);
        }
        FC87MSGIN fc87MSGIN = new FC87MSGIN();
        fc87MSGIN.setFCTRANID("FC87");
        fc87MSGIN.setFCCTRLID(branchNo);
        fc87MSGIN.setFCBRANCHNO(branchNo);
        fc87MSGIN.setFCWSID(workstationId);
        fc87MSGIN.setFCLTERM(terminalId);
        fc87MSGIN.setFCUSERID(userId);
        fc87MSGIN.setFCSEQNO(curSeqNo);
        fc87MSGIN.setFCDATE(curPDate);
        fc87MSGIN.setFCTIME(curTime);
        fc87MSGIN.setFCMODEFLAG("O");
        fc87MSGIN.setFCSTATUSFLAG("N");
        fc87MSGIN.setFCOVRHFLAG("01");
        fc87MSGIN.setFCAUTHOFFICER(userId);
        fc87MSGIN.setFCTRANLINK("001");
        fc87MSGIN.setFCINOPT("0");
        fc87MSGIN.setFCINNUMBER(String.format("%011d", Long.parseLong(customerNo)));
        vertx.executeBlocking(promise -> {
            try {
                FC87MSGOUT fc87MSGOUT = wsAcctStmtMessageSetPortType.fc87Opr1(fc87MSGIN);
                promise.complete(fc87MSGOUT);
            } catch (FaultMsg e) {
                deductionFailed(body, e);
                eb.publish(addressBook.getError(), body.put("error", "##### BankIranWS_NOR.CustomerInfoDB CustomerNo Fail " + customerNo + " FC87MSGOUT general Exception"), addressBook.getDeliveryOptions().addHeader("error", "FC87").addHeader("status", "ERROR"));
                errorOutboundCount++;
                //sendException(e);
                e.printStackTrace();
                promise.fail(e.getMessage());
            }
        }, res -> {
            if (res.succeeded()) {
                FC87MSGOUT fc87MSGOUT = (FC87MSGOUT) res.result();
                if (fc87MSGOUT.getALERTCODE().trim().isEmpty()) {
                    deductionFailed(body, "FC87MSGOUT_EMPTY_ALERT_CODE");
                    eb.publish(addressBook.getError(), body.put("error", "BankIranWS_NOR.CustomerInfoDB CustomerNo Fail " + customerNo + " Invalid Empty AlertCode " + fc87MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "FC87").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else if (Integer.parseInt(fc87MSGOUT.getALERTCODE()) >= Integer.parseInt(fc87MSGIN.getFCOVRHFLAG())) {
                    deductionFailed(body, fc87MSGOUT.getALERTMSG());
                    eb.publish(addressBook.getError(), body.put("error", "BankIranWS_NOR.CustomerInfoDB CustomerNo Fail " + customerNo + " " + fc87MSGOUT.getALERTMSG() + " " + fc87MSGOUT.getALERTCODE()), addressBook.getDeliveryOptions().addHeader("error", "FC87").addHeader("status", "ERROR"));
                    errorOutboundCount++;
                } else {
                    JsonObject fc87out = new JsonObject();
                    fc87out.put("Fill0", fc87MSGOUT.getFill0())
                            .put("HOSTCICSTRANSID", fc87MSGOUT.getHOSTCICSTRANSID())
                            .put("HOSTSEQNO", fc87MSGOUT.getHOSTSEQNO())
                            .put("HOSTDEST", fc87MSGOUT.getHOSTDEST())
                            .put("HOSTDATATYPE", fc87MSGOUT.getHOSTDATATYPE())
                            .put("HOSTDATACHAIN", fc87MSGOUT.getHOSTDATACHAIN())
                            .put("HOSTACKNOWLEDGE", fc87MSGOUT.getHOSTACKNOWLEDGE())
                            .put("HOSTMSGNBR", fc87MSGOUT.getHOSTMSGNBR())
                            .put("HOSTEMPTYSEND", fc87MSGOUT.getHOSTEMPTYSEND())
                            .put("FLGGLCD", fc87MSGOUT.getFLGGLCD())
                            .put("Fill1", fc87MSGOUT.getFill1())
                            .put("ALERTCODE", fc87MSGOUT.getALERTCODE())
                            .put("ALERTMSG", fc87MSGOUT.getALERTMSG())
                            .put("OUTMOBILENO", fc87MSGOUT.getOUTCUSTOMER().get(0).getOUTMOBILENO())
                            .put("OUTCUSTYPE", fc87MSGOUT.getOUTCUSTOMER().get(0).getOUTCUSTYPE())
                            .put("OUTCUSUBTYP", fc87MSGOUT.getOUTCUSTOMER().get(0).getOUTCUSUBTYP())
                            .put("CICSXT", fc87MSGOUT.getCICSXT());

                /*System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output Fill0           =>" + fc87MSGOUT.getFill0() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTCICSTRANSID =>" + fc87MSGOUT.getHOSTCICSTRANSID() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTSEQNO       =>" + fc87MSGOUT.getHOSTSEQNO() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTDEST        =>" + fc87MSGOUT.getHOSTDEST() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTDATATYPE    =>" + fc87MSGOUT.getHOSTDATATYPE() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTDATACHAIN   =>" + fc87MSGOUT.getHOSTDATACHAIN() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTACKNOWLEDGE =>" + fc87MSGOUT.getHOSTACKNOWLEDGE() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTMSGNBR      =>" + fc87MSGOUT.getHOSTMSGNBR() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output HOSTEMPTYSEND   =>" + fc87MSGOUT.getHOSTEMPTYSEND() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output FLGGLCD         =>" + fc87MSGOUT.getFLGGLCD() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output Fill1           =>" + fc87MSGOUT.getFill1() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output ALERTCODE       =>" + fc87MSGOUT.getALERTCODE() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output ALERTMSG        =>" + fc87MSGOUT.getALERTMSG() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output OUTCUSTOMER     =>" + fc87MSGOUT.getOUTCUSTOMER() + "<");
                System.out.println("##### " + customerNo + " BankIranWS_NOR.CustomerInfoDB output CICSXT          =>" + fc87MSGOUT.getCICSXT() + "<");*/

                    eb.publish(addressBook.getResult(), body.put("FC87MSGOUT", fc87out), addressBook.getDeliveryOptions().addHeader("status", "SUCCESS"));
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