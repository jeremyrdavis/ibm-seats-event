package com.redhat.demos;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class MyMessagingApplication {

    @Channel("thoughts-out")
    Emitter<ThoughtEvaluation> emitter;

    @Inject
    ThoughtEvaluator evaluator;

    @Incoming("thoughts-in")
    @ActivateRequestContext
    public void evaluate(Thought thought) {
        Log.debugf("Received thought: %s", thought);
        String verdict = evaluator.evaluate(thought.content());
        Log.infof("Thought evaluated as: %s", verdict);
        if ("APPROVED".equalsIgnoreCase(verdict.strip())) {
            Log.debugf("Thought has been approved. Sending to output channel: %s", thought);
            emitter.send(new ThoughtEvaluation(thought.id(), true));
        } else {
            Log.infof("Thought REJECTED: %s", thought.content());
            emitter.send(new ThoughtEvaluation(thought.id(), false));
        }
    }

}
