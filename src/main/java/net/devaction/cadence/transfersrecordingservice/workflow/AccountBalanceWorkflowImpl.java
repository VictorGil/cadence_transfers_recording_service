package net.devaction.cadence.transfersrecordingservice.workflow;

import java.math.BigDecimal;

import org.slf4j.Logger;

import com.uber.cadence.workflow.Workflow;

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
    public void addTransfer(final String id, final BigDecimal amount, final long transferTS) {
        Transfer transfer = new Transfer(id, amount, transferTS);
        transfers.add(transfer);
        balance = updateBalance(transfer);

        log.debug("New \"transfer\" added related to account with id \"{}\":\n{}\n"
                + "current account balance: {}", accountId, transfer, balance);
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
