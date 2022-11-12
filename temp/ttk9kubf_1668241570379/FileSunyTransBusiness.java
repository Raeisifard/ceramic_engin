import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.LocalMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import static com.vx6.utils.Library.correctPhoneNos;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class FileSunyTransBusiness extends MasterVerticle {
    private static final List<String> threeDigitCurrency = Arrays.asList("17", "26", "39", "84");
    private static final List<String> chequeStmtCodes
            = Arrays.asList("002", "056", "113", "412", "409", "410", "411", "413", "414", "415", "416", "417", "501");
    private String bankId;
    private LocalMap<String, String> stmts;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("Config")))
            initPromise.fail("Config data not found on datasource");
        else {
            bankId = config().getJsonObject("dataSource").getJsonObject("Config").getString("bankId");
            stmts = vertx.sharedData().getLocalMap("SAPTA_STMT");
            initPromise.complete();
        }
    }

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        if (tran.getString("ModeFlag").equals("I")){// inflight
            eb.publish(addressBook.getError(),body,addressBook.getDeliveryOptions(msg).addHeader("cause", "INFLIGHT"));
            errorOutboundCount++;
        }
        String accountNo = tran.getString("AccountNo");
        boolean currencyFlag = false;
        String currencyCode;
        if (!accountNo.startsWith("00")) {// if amount trans
            if (!(accountNo.startsWith("00", 10))) {
                currencyFlag = true;
            }
            currencyCode = accountNo.substring(10, 12);//Calculated but with no usage!
        }
        String amount = tran.getString("Amount");
        String balance = tran.getString("Balance");
        if (currencyFlag) {
            if (threeDigitCurrency.contains(accountNo.substring(10, 12))) {
                if (!amount.equals("0"))
                    amount = new StringBuilder(amount).insert(amount.length() - 3, ".").toString();
                if (!balance.equals("0"))
                    balance = new StringBuilder(balance).insert(balance.length() - 3, ".").toString();
            } else {
                if (!amount.equals("0"))
                    amount = new StringBuilder(amount).insert(amount.length() - 2, ".").toString();
                if (!balance.equals("0"))
                    balance = new StringBuilder(balance).insert(balance.length() - 2, ".").toString();
            }
        }
        String stmtCode = tran.getString("StmtCode");
        String variableData = tran.getString("VariableData");
        String chequeNumber = "          ";
        String optionalInfo = tran.getString("OptionalInfo");
        String module = tran.getString("Module");
        if (chequeStmtCodes.contains(stmtCode)) {
            if ((variableData.charAt(11) == ' ' || variableData.charAt(11) == ',') && !variableData.substring(1, 11).contains(" "))
                if (Library.isDigit(variableData.substring(1, 11))) {
                    chequeNumber = variableData.substring(1, 11);
                }
            if (variableData.charAt(7) == ' ' && !variableData.substring(1, 7).contains(" "))
                if (Library.isDigit(variableData.substring(1, 7))) {
                    if (!variableData.substring(8, 18).contains(" "))
                        if (Library.isDigit(variableData.substring(8, 18))) {
                            chequeNumber = variableData.substring(8, 18);
                        }
                }
            if ((module.startsWith("C46", 1)) || (module.startsWith("C56", 1))) {
                if (optionalInfo.charAt(17) == ' ' && !optionalInfo.substring(2, 17).contains(" ")) {
                    if (optionalInfo.charAt(1) == '+' || optionalInfo.charAt(1) == '-') {
                        amount = optionalInfo.substring(2, 17).trim();
                        if (!amount.isEmpty()) {
                            amount = String.format("%14s", Long.parseLong(amount));
                        }
                    }
                }
            }
        }
        String branchCode = tran.getString("BranchCode");
        if ((module.startsWith("C44", 1)) && (!chequeStmtCodes.contains(stmtCode))) {
            branchCode = "";
        }
        String chequeSerial = "";
        String chequeDate = "";
        if ((module.equals("DC44") || module.equals("FC44") || module.equals("ZC44")) && (stmtCode.equals("002") || stmtCode.equals("166") || stmtCode.equals("113"))) {
            chequeSerial = variableData.trim();
            chequeDate = optionalInfo.trim();
        }
        if (bankId.equalsIgnoreCase("bsi")) {
            String mod = module.substring(1, 4);
            if ((mod.equals("C44") && "002,007,113,164,371".contains(stmtCode)) ||//برداشت چک
                    mod.equals("C56")) {//برگشت چک
                chequeSerial = variableData.trim();
            }
        }
        String documentNumber = "";
        if (stmtCode.equalsIgnoreCase("001") && !(module.startsWith("TM") || module.startsWith("PM"))) {
            String variableDataTemp = variableData;
            variableDataTemp = variableDataTemp.substring(variableDataTemp.indexOf(" ") + 1).trim();
            if (Library.isDigit(variableDataTemp))
                documentNumber = variableDataTemp;
        }
        String txnKey = tran.getString("TxnKey");
        String traceNo = "";
        String cardPcc = "";
        String transBankCode = "";
        String paymentID = "";
        String termID = "";
        String cardNo = "";
        String sourceCardNo = null;
        if (module.startsWith("TM") || module.startsWith("PM")) {//333333 0544 17
            traceNo = txnKey.substring(14, 20);
            cardPcc = txnKey.substring(12, 14);
            if (Library.isDigit(variableData.substring(1, 7)) && variableData.charAt(7) == ' '
                    && Library.isDigit(variableData.substring(8, 12).trim()) && variableData.charAt(12) == ' '
                    && Library.isDigit(variableData.substring(13, 15))) {
                branchCode = variableData.substring(8, 12);
                if (variableData.charAt(15) == ' ')
                    transBankCode = variableData.substring(13, 15);
            } else if (Library.isDigit(variableData.substring(1, 16))) {
                paymentID = variableData.substring(1, 16);
            } else if (Library.isDigit(variableData.substring(1, 7)) && variableData.charAt(7) == ' '
                    && Library.isDigit(variableData.substring(8, 16).trim())) {
                traceNo = variableData.substring(1, 7);
                termID = variableData.substring(8, 16);
            }
            if (Library.isDigit(optionalInfo.substring(1, 17)) && optionalInfo.charAt(17) == ' ') {//6104337094134614 00000100 12
                cardNo = optionalInfo.substring(1, 17);
                sourceCardNo = optionalInfo.substring(13, 17);
                if (Library.isDigit(optionalInfo.substring(18, 26).trim()) && optionalInfo.charAt(26) == ' '
                        && Library.isDigit(optionalInfo.substring(27, 29))) {
                    //  branchCode = optionalInfo.substring(18, 22);
                    termID = optionalInfo.substring(18, 26);
                    if (optionalInfo.charAt(29) == ' ')
                        transBankCode = optionalInfo.substring(27, 29);
                    if (Library.isDigit(optionalInfo.substring(29)))
                        paymentID = paymentID + optionalInfo.substring(29);
                }
            }
        }
        amount = Library.getPureNumStr(amount);
        balance = Library.getPureNumStr(balance);
        if (module.startsWith("RW39")) {
            //  0000416735 70
            chequeNumber = variableData.substring(1, 11);
            transBankCode = variableData.substring(12, 14);
        }
        String forcedPhoneNo = "";
        if (module.equals("DC68") || module.equals("FC68") || module.equals("GC68") || module.equals("ZC68")
                || module.equals("ZC51") || module.equals("DC51") || module.equals("FC51")) {
            variableData = variableData.trim();
            if (variableData.length() == 11 && variableData.startsWith("09")) {
                forcedPhoneNo = variableData;
            }
        }
        if (module.equals("TN11") || module.equals("TC11")) {
            chequeNumber = variableData.trim();
            transBankCode = optionalInfo.trim();
        }
        String accountIdleDate = "";
        if (module.equals("BH27")) {
            accountIdleDate = variableData.trim();
        }
        String bondsCode = "";
        String bondsStep = "";
        String bondsAmount = "";
        if (module.equals("BC80")) {
            if (stmtCode.equals("320"))
                module = module + "A";
            if (stmtCode.equals("321"))
                module = module + "B";
            optionalInfo = optionalInfo.trim();
            bondsCode = optionalInfo.substring(0, 4);
            bondsStep = optionalInfo.substring(4, 6);
            bondsAmount = optionalInfo.substring(6, 24);
        }
        String bondsBranch = "";
        String bondsNo = "";
        String bondsLoanNo = "";
        if (module.equals("BC83")) {
            optionalInfo = optionalInfo.trim();
            bondsBranch = optionalInfo.substring(0, 4);
            bondsNo = optionalInfo.substring(4, 12);
            String flag = optionalInfo.substring(12, 13);
            bondsLoanNo = optionalInfo.substring(13, 26);
            if (flag.equals("G"))
                module = module + "A";
            if (flag.equals("E"))
                module = module + "B";
        }
        String chequeStartNo = "";
        String chequeEndNo = "";
        if (module.equals("RY70") ||
                module.equals("RY80") ||
                module.equals("FC5E") ||
                module.equals("FC5F") ||
                module.equals("FC49") /*|| module.equals("FC50")*/) {
            chequeStartNo = variableData.trim();
            chequeEndNo = optionalInfo.trim();
            if (chequeStartNo.equalsIgnoreCase(chequeEndNo) || chequeEndNo.startsWith("0000")) {
                chequeNumber = chequeStartNo;
                chequeStartNo = "";
                chequeEndNo = "";
            }
        }
        String customerNo = "";
        if (module.equals("ZC66") || module.equals("FC66") || module.equals("DC66")) {
            forcedPhoneNo = Library.correctPhoneNo(optionalInfo.substring(10, 21));
            customerNo = variableData.substring(1, 11).trim();
        }
        if (module.equals("FC50") || module.equals("ZC50") || module.equals("DC50") || module.equals("RT08")) {
            if (stmtCode.equals("033")) // مسدودی
                module = module + "A";
            if (stmtCode.equals("034"))  // رفع مسدودی
                module = module + "B";
        }
        String profileOldPhoneNo = "", profileNewPhoneNo = "";
        if (module.equals("FC93") || 
                module.equals("DC93") || 
                module.equals("ZC93") || 
                module.equals("RT6A") || 
                module.equals("RT78")) {
            customerNo = variableData.substring(1, 11).trim();
            profileOldPhoneNo = Library.correctPhoneNo(optionalInfo.substring(1, 12));
            profileNewPhoneNo = Library.correctPhoneNo(optionalInfo.substring(13, 24));
            if (!profileNewPhoneNo.equals(profileOldPhoneNo)) {
                module = "";
                forcedPhoneNo = "9999999999";
            } else {
                if (!profileOldPhoneNo.isEmpty())
                    forcedPhoneNo = profileOldPhoneNo;
                if (!profileNewPhoneNo.isEmpty())
                    if (forcedPhoneNo.isEmpty())
                        forcedPhoneNo = profileNewPhoneNo;
                    else
                        forcedPhoneNo = forcedPhoneNo + ";" + profileNewPhoneNo;
                accountNo = ""; // forced to be announced
            }
        }
        String tokenSerial = "";
        if (module.equals("SB20") || module.equals("BB20") || module.equals("ZB21") ||
                module.equals("SB30") || module.equals("BB30") || module.equals("ZB31")) {
            tokenSerial = variableData.trim();
        }
        String expireDate = "";
        if (module.equals("MT07") || module.equals("MT08")) {
            expireDate = optionalInfo.trim();
            forcedPhoneNo = tran.getString("Comment3").trim();
        }
        String chequeBankCode = "";
        if (module.equals("IM13")) {
            String[] chequeData = optionalInfo.trim().split(",");
            chequeBankCode = chequeData[0].substring(chequeData[0].length() - 2);
            chequeNumber = chequeData[1];
            chequeSerial = chequeData[2];
        }
        String tranIndicator = tran.getString("TransIndicator");
        if (tranIndicator.trim().isEmpty())
            tranIndicator = "N";
        String transId = module + tranIndicator;
        String uniqKey;
        if (txnKey.substring(0, 2).trim().length() == 2 && Integer.parseInt(txnKey.substring(0, 2)) >= 90)
            uniqKey = "13" + txnKey + accountNo; //+ module + transIndicator;
        else
            uniqKey = "14" + txnKey + accountNo;// + module + transIndicator;

        if (module.equalsIgnoreCase("IN45"))
            uniqKey = uniqKey + optionalInfo.trim();  // comment2
        if (StringUtils.isNotBlank(forcedPhoneNo)) {
            tran.put("phoneNos", correctPhoneNos(forcedPhoneNo));
        } else if (body.containsKey("accountInfo")) {
            tran.put("phoneNos", correctPhoneNos(body.getJsonObject("accountInfo").getString("Phones")));
            //TODO block non Sapta members txt here by checking Enable & DueDate fields in accountInfo.
        } else {
            publishOut(0, body, addressBook.getDeliveryOptions(msg));
            return;
        }
        tran.put("Balance", balance);
        tran.put("ChequeDate", chequeDate);
        tran.put("DocumentNumber", documentNumber);
        tran.put("CardPcc", cardPcc);
        tran.put("BranchCode", branchCode);
        tran.put("TraceNo", traceNo);
        tran.put("CardNo", cardNo);
        tran.put("SourceCardNo", sourceCardNo);
        tran.put("TermID", termID);
        tran.put("PaymentID", paymentID);
        tran.put("Amount", amount);
        tran.put("TransBankCode", transBankCode);
        tran.put("AccountNo", accountNo);
        tran.put("AccountIdleDate", accountIdleDate);
        tran.put("OptionalInfo", optionalInfo);
        tran.put("BondsCode", bondsCode);
        tran.put("BondsStep", bondsStep);
        tran.put("BondsAmount", bondsAmount);
        tran.put("BondsBranch", bondsBranch);
        tran.put("BondsNo", bondsNo);
        tran.put("BondsLoanNo", bondsLoanNo);
        tran.put("ChequeStartNo", chequeStartNo);
        tran.put("ChequeEndNo", chequeEndNo);
        tran.put("Module", module);
        tran.put("CustomerNo", customerNo);
        tran.put("ProfileOldPhoneNo", profileOldPhoneNo);
        tran.put("ProfileNewPhoneNo", profileNewPhoneNo);
        tran.put("TokenSerial", tokenSerial);
        tran.put("ForcedPhoneNo", forcedPhoneNo);
        tran.put("ExpireDate", expireDate);
        tran.put("ChequeSerial", chequeSerial);
        tran.put("ChequeNumber", chequeNumber.trim().length() > 0 ? chequeNumber.replaceFirst("^0+(?!$)", "") : null);
        tran.put("ChequeBankCode", chequeBankCode);
        tran.put("TranIndicator", tranIndicator);
        tran.put("TransId", transId);
        tran.put("uniqKey", uniqKey);
        if (StringUtils.isNotEmpty(stmtCode)) {
            String stmtDesc = stmts.get(stmtCode);
            if (StringUtils.isNotEmpty(stmtDesc)) {
                tran.put("StmtDesc", stmtDesc);
            }
        }
        eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
        resultOutboundCount++;
    }
    @Override
    public <T> void ready(Message<T> tMessage) {
        super.ready(tMessage);
        bankId = config().getJsonObject("dataSource").getJsonObject("Config").getString("bankId");
        stmts = vertx.sharedData().getLocalMap("SAPTA_STMT");
    }
}