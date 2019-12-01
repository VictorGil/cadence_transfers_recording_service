package net.devaction.cadence.example02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// We are aware that this class is not part of the Java API
// but we need it
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class ClientMain02 implements SignalHandler {
    private static final Logger log = LoggerFactory.getLogger(ClientMain02.class);

    private static final String WINCH_SIGNAL = "WINCH";

    private final WorkersManager workersManager = new WorkersManager();

    public static void main(String[] args) {
        new ClientMain02().run();
    }

    private void run() {
        log.info("Starting");
        registerThisAsOsSignalHandler();
        workersManager.start();
    }

    @Override
    public void handle(Signal osSignal) {
        log.info("We have received the Operating System signal to tell us to stop: {}", osSignal.getName());
        workersManager.stop();
        log.info("Exiting");
    }

    private void registerThisAsOsSignalHandler() {
        log.debug("Going to register this object to handle the {} signal", WINCH_SIGNAL);
        try {
            Signal.handle(new Signal(WINCH_SIGNAL), this);
        } catch (Exception ex) {
            // Most likely this is a signal that's not supported on this
            // platform or with the JVM as it is currently configured
            log.error("FATAL: The signal is not supported: {}, exiting", WINCH_SIGNAL, ex);
            System.exit(1);
        }
    }
}
