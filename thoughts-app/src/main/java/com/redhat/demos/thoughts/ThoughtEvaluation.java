package com.redhat.demos.thoughts;

/**
 * An immutable message representing the result of AI-based content moderation for a thought.
 *
 * <p>This is a Java {@code record}, which automatically generates a constructor, getters
 * ({@code id()}, {@code approved()}), {@code equals()}, {@code hashCode()}, and
 * {@code toString()}. Records are ideal for simple data carriers like messaging payloads.</p>
 *
 * <p>Instances are produced by the thoughts-evaluator microservice and consumed by
 * {@link EvaluationSink} via the "thoughts-evaluated" Kafka topic. Jackson automatically
 * serializes/deserializes records to/from JSON.</p>
 *
 * @param id       the database ID of the {@link Thought} that was evaluated
 * @param approved {@code true} if the AI moderator approved the thought for display,
 *                 {@code false} if the thought was rejected
 */
public record ThoughtEvaluation(Long id, boolean approved) {
}
