package net.devaction.cadence.accountbalanceworkflow;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;

import com.uber.cadence.workflow.Workflow;

import net.devaction.cadence.transfersrecordingservice.activity.PublishAccountBalanceActivity;
import net.devaction.entity.AccountBalanceEntity;
import net.devaction.entity.TransferEntity;

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
        TransferEntity transfer = new TransferEntity(transferId, accountId, amount, transferTS);
        transfers.add(transfer);
        balance = updateBalance(transfer);

        log.debug("New \"transfer\" added related to account with id \"{}\":\n{}\n"
                + "current account balance: {}", accountId, transfer, balance);

        final String clientId = "N/A";
        final long version = transfers.numberOfTransfers();
        publishBalanceActivity.publish(new AccountBalanceEntity(accountId, clientId, transferId, balance, version));
    }

    BigDecimal updateBalance(TransferEntity transfer) {
        return updateBalance(balance, transfer.getAmount());
    }

    BigDecimal updateBalance(BigDecimal balance, BigDecimal amount) {
        return balance.add(amount);
    }

    @Override
    public AccountBalanceEntity getBalance() {
        AccountBalanceEntity balanceEntity = new AccountBalanceEntity();
        balanceEntity.setAccountId(accountId);

        String latestTransferId = null;
        try {
            latestTransferId = transfers.getLatestTransferId();
        } catch (NoTransfersYetException e) {
            log.debug("There are no transfers yet for account id {}", accountId);
            latestTransferId = "There are no transfers yet";
        }
        balanceEntity.setTransferId(latestTransferId);

        balanceEntity.setBalance(balance);
        balanceEntity.setVersion(transfers.numberOfTransfers());
        // We may deprecate the client id field
        balanceEntity.setClientId("N/A");

        return balanceEntity;
    }

    @Override
    public BigDecimal getBalanceValue() {
        return balance;
    }

    @Override
    public List<TransferEntity> getTransfersList() {
        return transfers.getList();
    }

    @Override
    public void closeAccount() {
        log.info("Going to close the account with id \"{}\"", accountId);
        accountClosed = true;
    }

    public long getAccountBalanceVersion() {
        return (long) transfers.getList().size() + 1;
    }
}
