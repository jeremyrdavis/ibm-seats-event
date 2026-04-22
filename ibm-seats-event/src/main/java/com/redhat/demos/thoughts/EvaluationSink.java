package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class EvaluationSink {

    @Incoming("thoughts-evaluated")
    public void consume(ThoughtEvaluation evaluation) {
        Log.infof("Received evaluation for thought with id %d: approved=%b", evaluation.id(), evaluation.approved());
        QuarkusTransaction.requiringNew().run(() -> {
            Thought thought = Thought.findById(evaluation.id());
            if (thought != null) {
                if(evaluation.approved()) {
                    Log.infof("Thought with id %d has been APPROVED", evaluation.id());
                    thought.displayStatus = DisplayStatus.ENABLED;
                    Log.debugf("Thought with id %d has been updated to ENABLED", evaluation.id());
                } else {
                    Log.infof("Thought with id %d has been REJECTED", evaluation.id());
                    thought.displayStatus = DisplayStatus.DISABLED;
                    Log.warnf("Thought with id %d not found for evaluation", evaluation.id());
                }
            }
        });

    }
}
