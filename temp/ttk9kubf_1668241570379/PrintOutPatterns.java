import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;

public class PrintOutPatterns extends MasterVerticle {
    private static int count = 0;

    @Override
    public void process(Message msg) {
        resultOutboundCount--;
        System.out.println("pat#" + String.format("%03d", ++count) + ": " + msg.body().toString());
    }
}