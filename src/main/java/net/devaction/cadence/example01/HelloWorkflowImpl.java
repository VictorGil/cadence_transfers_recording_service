package net.devaction.cadence.example01;

import com.uber.cadence.workflow.Workflow;

import org.slf4j.Logger;

/**
 * @author Víctor Gil
 *
 * since November 2019
 */
public class HelloWorkflowImpl implements HelloWorkflow {

    private static Logger log = Workflow.getLogger(HelloWorkflowImpl.class);

    @Override
    public void sayHello(String name) {
        log.info("Hello {}!", name);
    }
}
