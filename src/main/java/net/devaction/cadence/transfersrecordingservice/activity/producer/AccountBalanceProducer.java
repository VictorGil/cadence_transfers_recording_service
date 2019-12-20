package net.devaction.cadence.transfersrecordingservice.activity.producer;

import net.devaction.entity.AccountBalanceEntity;

/**
 * @author Víctor Gil
 *
 * since August 2019
 */
public interface AccountBalanceProducer {

    public void start();

    public void send(AccountBalanceEntity accountBalanceEntity);

    public void stop();
}


