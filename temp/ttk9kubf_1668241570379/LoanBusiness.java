import com.vx6.master.MasterVerticle;
import com.vx6.utils.Library;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import static com.vx6.utils.Library.correctPhoneNos;

public class LoanBusiness extends MasterVerticle {
    private String bankId;

    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        if (!(config().containsKey("dataSource") && config().getJsonObject("dataSource").containsKey("Config")))
            initPromise.fail("Config data not found on datasource");
        else {
            bankId = config().getJsonObject("dataSource").getJsonObject("Config").getString("bankId");
            initPromise.complete();
        }
    }

    @Override
    public void process(Message msg) {
        JsonObject body = (JsonObject) msg.body();
        JsonObject tran = body.getJsonObject("tran");
        tran.put("CustomerName", tran.getString("CSTMRNM", tran.getString("CSTMR_NM", tran.getString("CUSTOMER_NM", tran.getString("CUSTMR_NM")))));
        String phoneNos = tran.getString("PHONE_NO", tran.getString("PHONENO", "")).trim();
        tran.put("bankId", bankId);
        if (tran.containsKey("DUE_DATE")) {
            tran.put("dueDate", Library.formatPDateStrLong(tran.getString("DUE_DATE")));
        }
        if (tran.containsKey("AMOUNT")) {
            tran.put("amount", Library.formatNumStrComma(tran.getString("AMOUNT")));
        }
        if (tran.containsKey("DFCT_AMOUNT")) {
            tran.put("DfctAmount", Library.formatNumStrComma(tran.getString("DFCT_AMOUNT")));
        }
        if (tran.containsKey("DRFT_AMNT")) {
            tran.put("DrftAmnt", Library.formatNumStrComma(tran.getString("DRFT_AMNT")));
        }
        if (tran.containsKey("FCLTY_AMNT") || tran.containsKey("FCLTYAMNT")) {
            tran.put("FcltyAmnt", Library.formatNumStrComma(tran.getString("FCLTY_AMNT", tran.getString("FCLTYAMNT"))));
        }
        if (tran.containsKey("INST_VAL")) {
            tran.put("InstVal", Library.formatNumStrComma(tran.getString("INST_VAL")));
        }
        if (tran.containsKey("INST_REM_VAL")) {
            tran.put("InstRemVal", Library.formatNumStrComma(tran.getString("INST_REM_VAL")));
        }
        if (tran.containsKey("REM_CRDT_AMNT")) {
            tran.put("RemCrdtAmnt", Library.formatNumStrComma(tran.getString("REM_CRDT_AMNT")));
        }
        if (tran.containsKey("RMNDR_AMNT")) {
            tran.put("RmndrAmnt", Library.formatNumStrComma(tran.getString("RMNDR_AMNT")));
        }
        if (tran.containsKey("UNPAID_INST_VAL")) {
            tran.put("UnpaidInstVal", Library.formatNumStrComma(tran.getString("UNPAID_INST_VAL")));
        }
        if (tran.containsKey("USD_CRDT_AMNT")) {
            tran.put("UsdCrdtAmnt", Library.formatNumStrComma(tran.getString("USD_CRDT_AMNT")));
        }
        if (tran.containsKey("REFOUND_AMNT")) {
            tran.put("RfndAmnt", Library.formatNumStrComma(tran.getString("REFOUND_AMNT")));
        }
        tran.put("transId", tran.getString("COMMAND"));
        tran.put("AccountNo", tran.getString("DEPSTACNTNO"));
        if (StringUtils.isNotEmpty(phoneNos)) {
            tran.put("phoneNos", correctPhoneNos(phoneNos));
            eb.publish(addressBook.getResult(), body, addressBook.getDeliveryOptions(msg));
        } else {
            resultOutboundCount--;
            errorOutboundCount++;
            eb.publish(addressBook.getError(), body, addressBook.getDeliveryOptions(msg));
        }

    }
}