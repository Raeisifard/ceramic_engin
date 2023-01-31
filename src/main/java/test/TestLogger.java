package test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestLogger {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("{}", 5);
        System.out.println("5");
    }
}
