package net.devaction.cadence.transfersrecordingservice;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.cadence.transfersrecordingservice.workflow.AccountBalanceWorkflowImpl;
import net.devaction.cadence.worker.WorkersCreator;
import net.devaction.kafka.avro.Transfer;
import net.devaction.kafka.consumer.KafkaConsumerWrapper;

// We are aware that this class is not part of the Java API
// but we need it
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class CadenceTransfersRecodingServiceMain implements SignalHandler {
    private static final Logger log = LoggerFactory.getLogger(CadenceTransfersRecodingServiceMain.class);

    private static final String WINCH_SIGNAL = "WINCH";

    private TransfersRecordingService service;

    public static void main(String[] args) {
        new CadenceTransfersRecodingServiceMain().run();
    }

    private void run() {
        log.info("Starting");
        registerThisAsOsSignalHandler();

        final String domain = "domain01";
        final String taskList = "taskList01";
        final Class<AccountBalanceWorkflowImpl> workflowClass = AccountBalanceWorkflowImpl.class;
        final int numOfWorkers = 5;

        final WorkersCreator<AccountBalanceWorkflowImpl> workersCreator = new WorkersCreator<>(
                domain, taskList, workflowClass, numOfWorkers);

        // TODO
        final KafkaConsumerWrapper<Transfer> kafkaConsumerWrapper = null;

        service = new TransfersRecordingServiceImpl(kafkaConsumerWrapper,
                workersCreator);

        service.start();
        log.info("End of the \"main\" thread");
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

    @Override
    public void handle(Signal osSignal) {
        log.info("We have received the Operating System signal to tell us to stop: {}", osSignal.getName());
        service.stop();

        // In theory, this should not be needed
        // but in reality, it is needed.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            log.error("{}", ex, ex);
        }
        log.info("Exiting");
    }
}
