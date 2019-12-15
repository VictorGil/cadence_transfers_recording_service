package net.devaction.cadence.transfersrecordingservice;

import net.devaction.kafka.avro.Transfer;
import net.devaction.kafka.consumer.KafkaConsumerWrapper;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class TransfersRecordingServiceImpl implements TransfersRecordingService {

    private final KafkaConsumerWrapper<Transfer> transferConsumer;

    public TransfersRecordingServiceImpl(KafkaConsumerWrapper<Transfer> transferConsumer) {
        this.transferConsumer = transferConsumer;
    }

    @Override
    public void start() {
        transferConsumer.start();
    }

    @Override
    public void stop() {
        transferConsumer.stop();
    }
}
