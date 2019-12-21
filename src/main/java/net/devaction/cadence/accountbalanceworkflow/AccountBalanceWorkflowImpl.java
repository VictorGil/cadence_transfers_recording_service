package net.devaction.cadence.accountbalanceworkflow;

import java.math.BigDecimal;

import org.slf4j.Logger;

import com.uber.cadence.workflow.Workflow;

import net.devaction.cadence.accountbalanceworkflow.AccountBalanceWorkflow;
import net.devaction.cadence.accountbalanceworkflow.Transfer;
import net.devaction.cadence.accountbalanceworkflow.Transfers;
import net.devaction.cadence.transfersrecordingservice.activity.PublishAccountBalanceActivity;
import net.devaction.entity.AccountBalanceEntity;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class AccountBalanceWorkflowImpl implements AccountBalanceWorkflow {

    private static final Logger log = Workflow.getLogger(AccountBalanceWorkflowImpl.class);

    private BigDecimal balance;

    private Transfers transfers;

    private String accountId;

    private boolean accountClosed;

    private final PublishAccountBalanceActivity publishBalanceActivity =
            Workflow.newActivityStub(PublishAccountBalanceActivity.class);

    @Override
    public void openAccount(String accountId) {
        log.info("Going to open a new account, account id (workflow id): \"{}\"", accountId);
        this.balance = BigDecimal.ZERO;
        this.accountId = accountId;
        this.transfers = new Transfers(accountId);

        Workflow.await(() -> accountClosed);
        log.info("Exiting workflow, account id: \"{}\", balance: {}", accountId, balance);
    }

    @Override
    public void addTransfer(final String transferId, final BigDecimal amount, final long transferTS) {
        Transfer transfer = new Transfer(transferId, amount, transferTS);
        transfers.add(transfer);
        balance = updateBalance(transfer);

        log.debug("New \"transfer\" added related to account with id \"{}\":\n{}\n"
                + "current account balance: {}", accountId, transfer, balance);

        final String clientId = "N/A";
        final long version = transfers.numberOfTransfers();
        publishBalanceActivity.publish(new AccountBalanceEntity(accountId, clientId, transferId, balance, version));
    }

    BigDecimal updateBalance(Transfer transfer) {
        return updateBalance(balance, transfer.getAmount());
    }

    BigDecimal updateBalance(BigDecimal balance, BigDecimal amount) {
        return balance.add(amount);
    }

    @Override
    public BigDecimal getBalance() {
        return balance;
    }

    @Override
    public Transfers getTransfers() {
        return transfers;
    }

    @Override
    public void closeAccount() {
        log.info("Going to close the account with id \"{}\"", accountId);
        accountClosed = true;
    }
}