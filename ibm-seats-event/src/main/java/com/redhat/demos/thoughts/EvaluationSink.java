package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EvaluationSink {

    @Incoming("thoughts-evaluated")
    public void consume(ThoughtEvaluation evaluation) {
        if(evaluation.approved()) {
            Log.infof("Thought with id %d has been APPROVED", evaluation.id());
        } else {
            Log.infof("Thought with id %d has been REJECTED", evaluation.id());
        }
    }
}
