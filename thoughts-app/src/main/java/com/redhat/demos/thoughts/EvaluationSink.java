package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 * Kafka consumer that receives AI moderation results and updates thought display status.
 *
 * <p>{@link ApplicationScoped @ApplicationScoped} makes this a CDI singleton bean — one instance
 * exists for the entire application lifecycle. This is the standard scope for Reactive Messaging
 * consumers, since they must remain active to continuously listen for incoming messages.</p>
 *
 * <p>This class closes the moderation loop: the {@link ThoughtResource} publishes new thoughts
 * to Kafka, the thoughts-evaluator microservice evaluates them with an IBM Granite LLM, and
 * this sink consumes the verdict and updates the database accordingly.</p>
 */
@ApplicationScoped
public class EvaluationSink {

    /**
     * Processes a moderation result received from the thoughts-evaluator microservice.
     *
     * <p>{@link Incoming @Incoming("thoughts-evaluated")} subscribes this method to the
     * "thoughts-evaluated" Reactive Messaging channel. The channel is mapped to a Kafka topic
     * in {@code application.properties}. SmallRye Reactive Messaging automatically deserializes
     * each Kafka record into a {@link ThoughtEvaluation} using Jackson.</p>
     *
     * <p>{@link QuarkusTransaction#requiringNew()} starts a new JTA transaction for the
     * database update. This is necessary because Reactive Messaging handlers do not run
     * inside a transaction by default. The thought's {@link DisplayStatus} is set to
     * {@link DisplayStatus#ENABLED} if approved or {@link DisplayStatus#DISABLED} if rejected.</p>
     *
     * @param evaluation the moderation result containing the thought's ID and approval status
     */
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
