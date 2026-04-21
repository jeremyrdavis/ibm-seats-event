package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.util.List;

@Path("/api/thoughts")
public class ThoughtResource {

    @POST
    public Thought addThought(Thought thought) {
        Log.debug("Adding thought: " + thought);
        thought.persist();
        Log.debug("Thought persisted: " + thought);
        return thought;
    }

    @GET
    public List<Thought> getThoughts() {
        Log.debug("Retrieving all thoughts");
        return Thought.listAll();
    }
}
