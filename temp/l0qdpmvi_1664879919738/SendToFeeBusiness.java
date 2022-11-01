import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;

public class SendToFeeBusiness extends MasterVerticle {
private static String feeBusinessAddress;
    @Override
    public void initialize(Promise<Void> initPromise) throws Exception {
        feeBusinessAddress = config.getString("feeBusinessAddress");
        initPromise.complete();
    }

    @Override
    public void process(Message msg) {
       eb.publish(feeBusinessAddress, msg.body(), addressBook.getDeliveryOptions(msg));
       eb.publish(addressBook.getResult(), msg.body(), addressBook.getDeliveryOptions(msg));
    }
}