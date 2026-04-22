package com.redhat.demos.thoughts;

/**
 * Represents the moderation lifecycle of a {@link Thought}.
 *
 * <p>When a thought is first submitted it starts as {@link #PENDING}. The thoughts-evaluator
 * microservice uses an IBM Granite LLM to assess the content and sends back a
 * {@link ThoughtEvaluation}. The {@link EvaluationSink} then transitions the thought to
 * {@link #ENABLED} (approved) or {@link #DISABLED} (rejected). Only {@code ENABLED} thoughts
 * are shown in the UI.</p>
 */
public enum DisplayStatus {
    /** The thought was rejected by the AI moderator and will not be displayed. */
    DISABLED,
    /** The thought was approved by the AI moderator and is visible in the UI. */
    ENABLED,
    /** The thought has been submitted but has not yet been evaluated. */
    PENDING;
}
