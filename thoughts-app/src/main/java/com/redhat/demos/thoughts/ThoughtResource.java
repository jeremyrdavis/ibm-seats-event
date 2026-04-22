package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import io.quarkus.narayana.jta.QuarkusTransaction;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import java.util.List;

/**
 * JAX-RS resource exposing the REST API for managing thoughts.
 *
 * <p>{@link Path @Path("/api/thoughts")} sets the base URI for all endpoints in this class.
 * Quarkus REST (formerly RESTEasy Reactive) discovers this class automatically at build time;
 * no {@code @ApplicationScoped} or registration is needed for JAX-RS resources.</p>
 */
@Path("/api/thoughts")
public class ThoughtResource {

    /**
     * A Reactive Messaging {@link Emitter} that sends {@link Thought} objects to the
     * "thoughts-created" Kafka topic.
     *
     * <p>{@link Channel @Channel("thoughts-created")} identifies the outgoing messaging channel.
     * The channel-to-topic mapping is configured in {@code application.properties}. Calling
     * {@code emitter.send()} serializes the Thought as JSON and publishes it to Kafka, where the
     * thoughts-evaluator microservice consumes it for AI-based moderation.</p>
     */
    @Channel("thoughts-created")
    Emitter<Thought> emitter;

    /**
     * Creates a new thought, persists it to the database, and publishes it to Kafka for evaluation.
     *
     * <p>{@link POST @POST} maps this method to HTTP POST requests at {@code /api/thoughts}.</p>
     *
     * <p>{@link Transactional @Transactional} wraps the method in a JTA transaction managed by
     * the Narayana transaction manager. This ensures that database operations are atomic — if an
     * exception occurs, all changes are rolled back.</p>
     *
     * <p>{@link Produces @Produces(MediaType.APPLICATION_JSON)} declares that this endpoint
     * returns JSON. This is important to prevent Jackson from intercepting other endpoints
     * (like Qute template responses) that produce different media types.</p>
     *
     * <p>{@link QuarkusTransaction#requiringNew()} starts a new, independent transaction for the
     * persist operation. This ensures the thought is committed to the database before the Kafka
     * message is sent, so the evaluator can look it up by ID when the evaluation result returns.</p>
     *
     * @param thought the thought to create, deserialized from the JSON request body by Jackson
     * @return the persisted thought (now including its generated {@code id})
     */
    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Thought addThought(Thought thought) {
        Log.debug("Adding thought: " + thought);

        QuarkusTransaction.requiringNew().run(() -> {
            thought.displayStatus = DisplayStatus.PENDING;
            thought.persist();
            Log.debug("Thought persisted in transaction: " + thought);
        });

        emitter.send(thought);

        Log.debug("Thought persisted: " + thought);
        return thought;
    }

    /**
     * Returns all thoughts in the database, regardless of their {@link DisplayStatus}.
     *
     * <p>{@link GET @GET} maps this method to HTTP GET requests at {@code /api/thoughts}.</p>
     *
     * <p>{@link Produces @Produces(MediaType.APPLICATION_JSON)} ensures the response is
     * serialized as a JSON array by Jackson.</p>
     *
     * <p>{@code Thought.listAll()} is a Panache active record method that executes
     * {@code SELECT * FROM Thought} and returns all rows as entity instances.</p>
     *
     * @return a list of all thoughts
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Thought> getThoughts() {
        Log.debug("Retrieving all thoughts");
        return Thought.listAll();
    }

}
