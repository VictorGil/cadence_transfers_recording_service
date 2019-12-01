package net.devaction.cadence.example02;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.worker.Worker;
import com.uber.cadence.worker.Worker.Factory;
import com.uber.cadence.worker.WorkerOptions;

import net.devaction.cadence.example01.HelloWorkflowImpl;

/**
 * @author Víctor Gil
 *
 * since December 2019
 */
public class WorkersManager {
    private static final Logger log = LoggerFactory.getLogger(WorkersManager.class);

    private static final String DOMAIN = "test-domain";
    private static final String TASK_LIST = "hello-task-list";
    private final Factory factory = new Worker.Factory(DOMAIN);

    void start() {
        for (int i = 1; i <= 10; i++) {
            log.info("Creating worker runnable number {}", i);
            WorkerOptions.Builder woBuilder = new WorkerOptions.Builder();
            woBuilder.setIdentity("worker-" + i);
            Worker worker = factory.newWorker(TASK_LIST, woBuilder.build());
            worker.registerWorkflowImplementationTypes(HelloWorkflowImpl.class);
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
}
