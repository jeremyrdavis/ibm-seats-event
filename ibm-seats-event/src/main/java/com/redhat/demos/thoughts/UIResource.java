package com.redhat.demos.thoughts;

import io.quarkus.logging.Log;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.qute.TemplateInstance;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;
import java.util.Random;

@Path("/ui")
public class UIResource {

    @CheckedTemplate
    public static class Templates {
        public static native TemplateInstance randomThought(Thought thought);
        public static native TemplateInstance allThoughts(List<Thought> thoughts);
    }

    @GET
    @Path("/random")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getRandomThought() {
        Log.debug("Retrieving random thought");
        List<Thought> thoughts = Thought.find("displayStatus", DisplayStatus.ENABLED).list();
        if (thoughts.isEmpty()) {
            return Templates.randomThought(new Thought("No thoughts found.", "System", ""));
        }
        int index = new Random().nextInt(thoughts.size());
        Thought randomThought = thoughts.get(index);
        Log.debugf("Selected random thought: %s", randomThought);
        return Templates.randomThought(randomThought);
    }

    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllThoughts() {
        Log.debug("Retrieving all thoughts");
        return Templates.allThoughts(Thought.find("displayStatus", DisplayStatus.ENABLED).list());
    }
}
