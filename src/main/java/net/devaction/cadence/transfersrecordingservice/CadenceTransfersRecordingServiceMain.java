package net.devaction.cadence.transfersrecordingservice;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.cadence.transfersrecordingservice.config.ServiceConfigReader;
import net.devaction.cadence.transfersrecordingservice.config.ServiceConfigValues;
import net.devaction.cadence.transfersrecordingservice.processor.TransferProcessor;
import net.devaction.kafka.avro.Transfer;
import net.devaction.kafka.consumer.ConsumerOptions;
import net.devaction.kafka.consumer.KafkaConsumerWrapper;
import net.devaction.kafka.consumer.KafkaConsumerWrapperImpl;

// We are aware that this class is not part of the Java API
// but we need it
import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class CadenceTransfersRecordingServiceMain implements SignalHandler {
    private static final Logger log = LoggerFactory.getLogger(CadenceTransfersRecordingServiceMain.class);

    private static final String WINCH_SIGNAL = "WINCH";

    private TransfersRecordingService service;
    private TransferProcessor transferProcessor;

    public static void main(String[] args) {
        new CadenceTransfersRecordingServiceMain().run();
    }

    private void run() {
        log.info("Starting");
        registerThisAsOsSignalHandler();

        ServiceConfigValues values = readConfigValues();
        final String domain = values.getCadenceDomain();

        transferProcessor = new TransferProcessor(domain);
        transferProcessor.start();

        ConsumerOptions<Transfer>.Builder optionsBuilder = new ConsumerOptions<Transfer>().newBuilder();
        ConsumerOptions<Transfer> options = optionsBuilder.setBootstrapServers(values.getKafkaBootstrapServers())
                .setSchemaRegistryUrl(values.getKafkaSchemaRegistryUrl())
                .setTopic(values.getKafkaTransfersTopic())
                .setPollingMillis(values.getKafkaPollingMillis())
                .setProcessor(transferProcessor)
                .setSeekFromBeginning(true).build();

        final KafkaConsumerWrapper<Transfer> kafkaConsumerWrapper = new KafkaConsumerWrapperImpl<>(options);

        service = new TransfersRecordingServiceImpl(kafkaConsumerWrapper);

        // The execution of the \"main\" thread will block on the statement below
        service.start();
        log.info("End of the \"main\" thread");
    }

    private ServiceConfigValues readConfigValues() {
        final ServiceConfigReader reader = new ServiceConfigReader();
        ServiceConfigValues values = null;

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
        service.stop();
        transferProcessor.stop();

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
