package test;

import io.vertx.core.json.JsonObject;
import org.apache.logging.log4j.*;
import com.splunk.logging.*;

import java.util.Random;

class Scratch {
    private static final Logger logger = org.apache.logging.log4j.core.LoggerContext.getContext().getLogger("splunk.logger");
    private static Random rand = new Random();

    public static void main(String[] args) {
        int r = rand.nextInt(101);
        for (int i = 0; i < r; i++) {
            logger.info("This is a test event for Log4j test " + rand.nextInt(101) + " No. " + i);
            //logger.error("This is a test error for Log4j test " + rand.nextInt(101));
        }
        System.out.printf("Logged out for " + r + " iterations");
    }
}
