package net.devaction.cadence.example01;

import com.uber.cadence.workflow.WorkflowMethod;

/**
 * @author Víctor Gil
 *
 * since November 2019
 */
public interface HelloWorkflow {

    @WorkflowMethod
    void sayHello(String name);
}
