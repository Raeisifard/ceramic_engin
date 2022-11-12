import com.vx6.master.MasterVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;


public class HeaderAndBodyProbe extends MasterVerticle {
    private final String className = this.getClass().getName();

    @Override
    public void process(Message msg) {
        System.out.println(className + "(" + config().getString("id") + ").Input.body: " + msg.body());
        System.out.println(className + "(" + config().getString("id") + ").Input.header: " + msg.headers());
    }

    @Override
    public void trigger(Message msg) {
        System.out.println(className + "(" + config().getString("id") + ").Trigger.body: " + msg.body());
        System.out.println(className + "(" + config().getString("id") + ").Trigger.header: " + msg.headers());
    }
    
    @Override
    public <T> void ready(Message<T> tMessage) {
        /*holdOn = false;
        if (autoNext) {
            eb.publish(addressBook.getTrigger(), "Next message", new DeliveryOptions().addHeader("cmd", "next"));
        }*/
    }
}