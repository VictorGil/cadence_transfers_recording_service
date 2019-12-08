package net.devaction.cadence.integrationtest.workflow;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.client.WorkflowClient;

import net.devaction.cadence.transfersrecordingservice.workflow.AccountBalanceWorkflow;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class AddTransferRequesterMain {
    private static final Logger log = LoggerFactory.getLogger(AddTransferRequesterMain.class);

    public static void main(String[] args) {
        new AddTransferRequesterMain().run();
    }

    private void run() {
        WorkflowClient workflowClient = WorkflowClient.newInstance("domain01");
        AccountBalanceWorkflow account02wf = workflowClient.newWorkflowStub(AccountBalanceWorkflow.class,
                "account02");
        account02wf.addTransfer(generateRandomId(), BigDecimal.valueOf(1001, 0),
                currentTime());

        log.info("Going to close the workflow client");
        workflowClient.close();
        log.info("The workflow client has been closed");
    }

    private String generateRandomId() {
        // last 12 hexadecimal digits of the random UUID
        return UUID.randomUUID().toString().substring(24);
    }

    private long currentTime() {
        return Clock.systemUTC().instant().toEpochMilli();
    }
}
