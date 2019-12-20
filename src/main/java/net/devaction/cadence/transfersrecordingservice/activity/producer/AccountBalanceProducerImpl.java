package net.devaction.cadence.transfersrecordingservice.activity.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.devaction.entity.AccountBalanceEntity;
import net.devaction.kafka.avro.AccountBalance;
import net.devaction.kafka.avro.util.AccountBalanceConverter;
import net.devaction.kafka.producer.KafkaProducerWrapper;
import net.devaction.kafka.producer.KafkaProducerWrapperImpl;

/**
 * @author Víctor Gil
 *
 * since August 2019
 */
public class AccountBalanceProducerImpl implements AccountBalanceProducer {
    private static final Logger log = LoggerFactory.getLogger(AccountBalanceProducerImpl.class);

    private KafkaProducerWrapper<AccountBalance> producer;
    private final String accountBalanceTopic;

    public AccountBalanceProducerImpl(String bootstrapServers, String schemaRegistryUrl,
            String accountBalanceTopic) {

        producer = new KafkaProducerWrapperImpl<>(bootstrapServers, schemaRegistryUrl);
        this.accountBalanceTopic = accountBalanceTopic;
    }

    public void start() {
        producer.start();
    }

    @Override
    public void send(AccountBalanceEntity accountBalanceEntity) {
        log.info("Going to send/produce/publish the following account balance data: {}",
                accountBalanceEntity);

        AccountBalance accountBalance = AccountBalanceConverter.convertToAvro(accountBalanceEntity);
        producer.send(accountBalanceTopic, accountBalance.getAccountId(), accountBalance);
    }

    @Override
    public void stop() {
        producer.stop();
    }
}
