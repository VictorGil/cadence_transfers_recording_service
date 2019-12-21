package net.devaction.cadence.transfersrecordingservice;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.cadence.transfersrecordingservice.config.WorkersConfigReader;
import net.devaction.cadence.transfersrecordingservice.config.WorkersConfigValues;
import net.devaction.cadence.accountbalanceworkflow.AccountBalanceWorkflowImpl;
import net.devaction.cadence.worker.WorkersCreator;

// We are aware that this class is not part of the Java API
// but we need it
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class WorkersCreatorMain implements SignalHandler {
    private static final Logger log = LoggerFactory.getLogger(WorkersCreatorMain.class);

    private static final String WINCH_SIGNAL = "WINCH";

    private WorkersCreator<AccountBalanceWorkflowImpl> workersCreator;

    public static void main(String[] args) {
        new WorkersCreatorMain().run();
    }

    private void run() {
        log.info("Starting");
        registerThisAsOsSignalHandler();

        WorkersConfigValues values = readConfigValues();

        final String domain = values.getCadenceDomain();
        final int numOfWorkers = values.getCadenceWorkers();
        final String taskList = "taskList01";
        final Class<AccountBalanceWorkflowImpl> workflowClass = AccountBalanceWorkflowImpl.class;

        final String bootstrapServers = values.getKafkaBootstrapServers();
        final String schemaRegistryUrl = values.getKafkaSchemaRegistryUrl();
        final String accountBalanceTopic = values.getKafkaAccountBalancesTopic();

        workersCreator = new WorkersCreator<>(domain, taskList, workflowClass, numOfWorkers,
                bootstrapServers, schemaRegistryUrl, accountBalanceTopic);
        workersCreator.start();
    }

    private WorkersConfigValues readConfigValues() {
        final WorkersConfigReader reader = new WorkersConfigReader();
        WorkersConfigValues values = null;

        try {
            values = reader.read();
        } catch (Exception ex) {
            log.error("Unable to read configuration values, exiting");
            System.exit(1);
        }

        return values;
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
        workersCreator.stop();

        // In theory, this should not be needed
        // but in reality, it is needed.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException ex) {
            log.error("{}", ex, ex);
            Thread.currentThread().interrupt();
        }
        log.info("Exiting");
    }
}
