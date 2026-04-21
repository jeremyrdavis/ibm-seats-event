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

@Path("/api/thoughts")
public class ThoughtResource {

    @Channel("thoughts-created")
    Emitter<Thought> emitter;

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Thought addThought(Thought thought) {
        Log.debug("Adding thought: " + thought);

        QuarkusTransaction.requiringNew().run(() -> {
            thought.persist();
            Log.debug("Thought persisted in transaction: " + thought);
        });

        emitter.send(thought);

        Log.debug("Thought persisted: " + thought);
        return thought;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Thought> getThoughts() {
        Log.debug("Retrieving all thoughts");
        return Thought.listAll();
    }

}
