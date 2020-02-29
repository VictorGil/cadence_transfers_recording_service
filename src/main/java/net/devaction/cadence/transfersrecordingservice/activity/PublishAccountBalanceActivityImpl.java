package net.devaction.cadence.transfersrecordingservice.activity;

import net.devaction.cadence.transfersrecordingservice.activity.producer.AccountBalanceProducer;
import net.devaction.entity.AccountBalanceEntity;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class PublishAccountBalanceActivityImpl implements PublishAccountBalanceActivity {

    private final AccountBalanceProducer producer;

    public PublishAccountBalanceActivityImpl(AccountBalanceProducer producer) {
        this.producer = producer;
    }

    @Override
    public void publish(AccountBalanceEntity accountBalance) {
        producer.send(accountBalance);
    }
}
