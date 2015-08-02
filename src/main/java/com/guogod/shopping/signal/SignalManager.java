package com.guogod.shopping.signal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Created by shijunbo on 2015/7/15.
 */
public class SignalManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignalManager.class);

    public static void install() {
        Signal.handle(new Signal("TERM"), new QuitSignalHandler());
        Signal.handle(new Signal("INT"), new QuitSignalHandler());
        LOGGER.info("signal handlers installed");
    }

    static class QuitSignalHandler implements SignalHandler {
        @Override
        public void handle(Signal signal) {
            LOGGER.info("signal:{} received", signal.getName());
            System.exit(0);
        }
    }
}
