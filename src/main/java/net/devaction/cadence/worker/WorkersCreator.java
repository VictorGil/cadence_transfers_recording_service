package net.devaction.cadence.worker;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.WorkerOptions;
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

    private Factory factory;

    public WorkersCreator(String domain, String taskList, Class<T> workflowClass, int numOfWorkers) {
        this.domain = domain;
        this.taskList = taskList;
        this.workflowClass = workflowClass;
        this.numOfWorkers = numOfWorkers;
    }

    public void start() {
        this.factory = new Worker.Factory(domain);

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
    }

    void createWorker(int index) {
        log.info("Creating worker number {}", index);

        WorkerOptions.Builder woBuilder = new WorkerOptions.Builder();
        woBuilder.setIdentity(workflowClass.getSimpleName() + "-worker-" + index);
        Worker worker = factory.newWorker(taskList, woBuilder.build());
        worker.registerWorkflowImplementationTypes(workflowClass);
    }
}
