package net.devaction.cadence.integrationtest.workflow;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.client.WorkflowClient;

import net.devaction.cadence.accountbalanceworkflow.AccountBalanceWorkflow;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class GetBalanceValueQueryMain {
    private static final Logger log = LoggerFactory.getLogger(GetBalanceValueQueryMain.class);

    public static void main(String[] args) {
        new GetBalanceValueQueryMain().run();
    }

    private void run() {
        final WorkflowClient workflowClient = WorkflowClient.newInstance("domain01");

        final String accountId = "account-01";
        final AccountBalanceWorkflow accountBalanceWf = workflowClient.newWorkflowStub(AccountBalanceWorkflow.class,
                accountId);

        final BigDecimal balance = accountBalanceWf.getBalanceValue();
        log.info("The current balance of the \"{}\" account is {}", accountId, balance);

        log.info("Going to close the workflow client");
        workflowClient.close();
        log.info("The workflow client has been closed");
    }
}
