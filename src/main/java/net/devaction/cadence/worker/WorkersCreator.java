package net.devaction.cadence.worker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;

import net.devaction.cadence.transfersrecordingservice.activity.PublishAccountBalanceActivityImpl;
import net.devaction.cadence.transfersrecordingservice.activity.producer.AccountBalanceProducer;
import net.devaction.cadence.transfersrecordingservice.activity.producer.AccountBalanceProducerImpl;

import com.uber.cadence.worker.Worker.Factory;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class WorkersCreator<T> {
    private static final Logger log = LoggerFactory.getLogger(WorkersCreator.class);

    private final String domain;
    private final String taskList;
    private final Class<T> workflowClass;
    private final int numOfWorkers;

    private final String bootstrapServers;
    private final String schemaRegistryUrl;
    private final String accountBalanceTopic;

    private final List<AccountBalanceProducer> producers = new ArrayList<>();
    private Factory factory;

    public WorkersCreator(String domain, String taskList, Class<T> workflowClass, int numOfWorkers,
            String bootstrapServers, String schemaRegistryUrl, String accountBalanceTopic) {
        this.domain = domain;
        this.taskList = taskList;
        this.workflowClass = workflowClass;
        this.numOfWorkers = numOfWorkers;

        this.bootstrapServers = bootstrapServers;
        this.schemaRegistryUrl = schemaRegistryUrl;
        this.accountBalanceTopic = accountBalanceTopic;
    }

    public void start() {
        this.factory = new Worker.Factory(domain);

        createAndStartProducers();

        for (int i = 1; i <= numOfWorkers; i++) {
            createWorker(i);
        }

        factory.start();
    }

    public void stop() {
        log.info("We have been told to stop, shutting down all the workiers");
        factory.shutdown();
        log.info("Waiting for all the workers to come down");
        factory.awaitTermination(1L, TimeUnit.HOURS);
        log.info("All workers are down");

        for (AccountBalanceProducer producer : producers) {
            producer.stop();
        }
        log.info("All {} kafka producers have been stopped.", producers.size());
    }

    void createAndStartProducers() {
        for (int i = 1; i <= numOfWorkers; i++) {
            AccountBalanceProducer producer = new AccountBalanceProducerImpl(bootstrapServers, schemaRegistryUrl, accountBalanceTopic);
            producer.start();
            producers.add(producer);
        }
    }

    void createWorker(int index) {
        log.info("Creating worker number {}", index);

        WorkerOptions.Builder woBuilder = new WorkerOptions.Builder();
        woBuilder.setIdentity(workflowClass.getSimpleName() + "-worker-" + index);
        Worker worker = factory.newWorker(taskList, woBuilder.build());
        worker.registerWorkflowImplementationTypes(workflowClass);

        worker.registerActivitiesImplementations(new PublishAccountBalanceActivityImpl(producers.get(index - 1)));
    }
}
