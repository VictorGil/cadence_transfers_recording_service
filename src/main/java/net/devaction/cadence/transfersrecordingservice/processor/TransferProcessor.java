package net.devaction.cadence.transfersrecordingservice.processor;

import java.math.BigDecimal;
import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;

import net.devaction.cadence.transfersrecordingservice.workflow.AccountBalanceWorkflow;
import net.devaction.entity.TransferEntity;
import net.devaction.kafka.avro.Transfer;
import net.devaction.kafka.avro.util.TransferConverter;
import net.devaction.kafka.consumer.Processor;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class TransferProcessor implements Processor<Transfer> {
    private static final Logger log = LoggerFactory.getLogger(TransferProcessor.class);

    private final String domain;

    private WorkflowClient workflowClient;

    public TransferProcessor(String domain) {
        this.domain = domain;
    }

    public void start() {
        workflowClient = WorkflowClient.newInstance(domain);
    }

    public void stop() {
        log.info("Going to close the workflow client");
        workflowClient.close();
        log.info("The workflow client has been closed");
    }

    @Override
    public void process(Transfer transfer) {
        TransferEntity entity = TransferConverter.convertToPojo(transfer);
        log.trace("{} data to be processed: {}", TransferEntity.class.getSimpleName(), entity);

        process(entity);
    }

    void process(TransferEntity transferEntity) {
        if (transferEntity.getAmount().compareTo(BigDecimal.ZERO) == 0) {
            // This is an (arbitrary) business rule, it means that we need to create a new account
            openAccount(transferEntity.getAccountId());
        } else {
            addTransfer(transferEntity.getAccountId(), transferEntity.getId(), transferEntity.getAmount(),
                    transferEntity.getTransferTS());
        }
    }

    void addTransfer(String accountId, String transferId, BigDecimal amount, long timestamp) {
        AccountBalanceWorkflow workflow = workflowClient.newWorkflowStub(AccountBalanceWorkflow.class,
                accountId);

        // We send a signal to the existing workflow
        workflow.addTransfer(transferId, amount, timestamp);
    }

    void openAccount(String accountId) {
        final WorkflowOptions workflowOptions =
                new WorkflowOptions.Builder()
                    .setTaskList("taskList01")
                    .setExecutionStartToCloseTimeout(Duration.ofDays(10000))
                    .setWorkflowId(accountId)
                    .build();

        AccountBalanceWorkflow workflow = workflowClient.newWorkflowStub(AccountBalanceWorkflow.class,
                workflowOptions);

        // We start a new workflow
        WorkflowClient.start(workflow::openAccount, accountId);
    }
}
