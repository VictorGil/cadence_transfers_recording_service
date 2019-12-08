package net.devaction.cadence.integrationtest.workflow;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.client.WorkflowClient;
import com.uber.cadence.client.WorkflowOptions;

import net.devaction.cadence.transfersrecordingservice.workflow.AccountBalanceWorkflow;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class OpenAccountRequesterMain {
    private static final Logger log = LoggerFactory.getLogger(OpenAccountRequesterMain.class);

    public static void main(String[] args) {
        new OpenAccountRequesterMain().run();
    }

    private void run() {
        final WorkflowClient workflowClient = WorkflowClient.newInstance("domain01");
        final WorkflowOptions workflowOptions =
                new WorkflowOptions.Builder()
                    .setTaskList("taskList01")
                    .setExecutionStartToCloseTimeout(Duration.ofDays(10000))
                    .setWorkflowId("account02")
                    .build();

        AccountBalanceWorkflow workflow = workflowClient.newWorkflowStub(AccountBalanceWorkflow.class,
                workflowOptions);

        WorkflowClient.start(workflow::openAccount, "account02");
        log.info("Going to close the workflow client");
        workflowClient.close();
        log.info("The workflow client has been closed");
    }
}
