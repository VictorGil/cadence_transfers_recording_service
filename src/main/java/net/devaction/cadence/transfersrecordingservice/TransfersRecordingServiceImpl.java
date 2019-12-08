package net.devaction.cadence.transfersrecordingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.kafka.avro.Transfer;
import net.devaction.kafka.consumer.KafkaConsumerWrapper;

import net.devaction.cadence.transfersrecordingservice.workflow.AccountBalanceWorkflowImpl;
import net.devaction.cadence.worker.WorkersCreator;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class TransfersRecordingServiceImpl implements TransfersRecordingService {
    private static final Logger log = LoggerFactory.getLogger(TransfersRecordingServiceImpl.class);

    private final KafkaConsumerWrapper<Transfer> transferConsumer;

    private final WorkersCreator<AccountBalanceWorkflowImpl> workersCreator;

    public TransfersRecordingServiceImpl(KafkaConsumerWrapper<Transfer> transferConsumer,
            WorkersCreator<AccountBalanceWorkflowImpl> workersCreator) {
        this.transferConsumer = transferConsumer;
        this.workersCreator = workersCreator;
    }

    @Override
    public void start() {
        workersCreator.start();
        transferConsumer.start();
    }

    @Override
    public void stop() {
        transferConsumer.stop();
        workersCreator.stop();
    }
}
