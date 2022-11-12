import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import static com.vx6.utils.Library.correctPhoneNos;

public class SendSMSBusiness extends MasterVerticle {
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
        String phoneNos = tran.getString("PHONE_NO", tran.getString("PHONENO", "")).trim();
        tran.put("bankId", bankId);
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