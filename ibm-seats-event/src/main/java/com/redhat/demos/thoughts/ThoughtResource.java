package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Random;

@Path("/api/thoughts")
public class ThoughtResource {

    @POST
    @Transactional
    @Produces(MediaType.APPLICATION_JSON)
    public Thought addThought(Thought thought) {
        Log.debug("Adding thought: " + thought);
        thought.persist();
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
