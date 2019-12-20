package net.devaction.cadence.transfersrecordingservice.activity;

import com.uber.cadence.activity.ActivityMethod;

import net.devaction.entity.AccountBalanceEntity;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public interface PublishAccountBalanceActivity {

    @ActivityMethod(scheduleToCloseTimeoutSeconds = 100)
    void publish(AccountBalanceEntity accountBalance);
}
