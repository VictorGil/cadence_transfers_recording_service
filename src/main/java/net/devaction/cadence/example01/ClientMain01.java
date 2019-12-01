package net.devaction.cadence.example01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.uber.cadence.worker.Worker.Factory;
import com.uber.cadence.worker.Worker;

/**
 * @author Víctor Gil
 *
 * since November 2019
 */
public class ClientMain01 {
    private static final Logger log = LoggerFactory.getLogger(ClientMain01.class);

    private static final String DOMAIN = "test-domain";
    private static final String TASK_LIST = "hello-task-list";

    public static void main(String[] args) {
        log.info("Starting.");

        final Factory factory = new Worker.Factory(DOMAIN);
        final Worker worker = factory.newWorker(TASK_LIST);
        worker.registerWorkflowImplementationTypes(HelloWorkflowImpl.class);
        factory.start();

        log.info("Exiting.");
    }
}
