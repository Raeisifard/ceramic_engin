import com.vx6.master.MasterVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import com.splunk.logging.*;

import java.util.Random;

public class Probe26 extends MasterVerticle {
    private static Random rand = new Random();
    //private static final Logger logger = org.apache.logging.log4j.core.LoggerContext.getContext().getLogger("splunk.logger");
    private static final Logger logger = LoggerFactory.getLogger("splunk.logger");

    @Override
    public void process(Message msg) {
        int r = rand.nextInt(101);
        for (int i = 0; i < r; i++) {
            logger.info("This is a test event for Log4j test " + rand.nextInt(201) + " No. " + i);
            //logger.error("This is a test error for Log4j test " + rand.nextInt(101));
        }
        //System.out.printf("Logged out for " + r + " iterations");
    }
}