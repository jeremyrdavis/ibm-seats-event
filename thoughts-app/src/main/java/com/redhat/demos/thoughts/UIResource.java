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

/**
 * JAX-RS resource that serves server-side rendered HTML pages using the Qute templating engine.
 *
 * <p>{@link Path @Path("/ui")} sets the base URI. This resource is separate from
 * {@link ThoughtResource} because it produces {@code text/html} rather than JSON, and serves
 * the end-user-facing views.</p>
 */
@Path("/ui")
public class UIResource {

    /**
     * Qute checked template declarations for this resource.
     *
     * <p>{@link CheckedTemplate @CheckedTemplate} enables compile-time validation of Qute
     * templates. Each {@code native} method declaration maps to a template file at
     * {@code src/main/resources/templates/UIResource/<methodName>.html}. The method parameters
     * become the template's type-safe data variables. If a template file is missing or a
     * variable name doesn't match, the Quarkus build fails — catching errors early rather
     * than at runtime.</p>
     */
    @CheckedTemplate
    public static class Templates {
        /**
         * Renders a single thought as an HTML card.
         *
         * @param thought the thought to display
         * @return a {@link TemplateInstance} that Quarkus REST renders as the HTTP response body
         */
        public static native TemplateInstance randomThought(Thought thought);

        /**
         * Renders all approved thoughts as an HTML list.
         *
         * @param thoughts the list of thoughts to display
         * @return a {@link TemplateInstance} that Quarkus REST renders as the HTTP response body
         */
        public static native TemplateInstance allThoughts(List<Thought> thoughts);
    }

    /**
     * Selects a random approved thought and renders it as an HTML card.
     *
     * <p>{@link GET @GET} maps to HTTP GET. {@link Path @Path("/random")} appends to the class-level
     * path, making the full URL {@code /ui/random}.</p>
     *
     * <p>{@link Produces @Produces(MediaType.TEXT_HTML)} tells Quarkus REST that this endpoint
     * returns HTML. This ensures the Qute {@link TemplateInstance} is rendered by Qute's
     * MessageBodyWriter rather than being serialized as JSON by Jackson.</p>
     *
     * <p>Only thoughts with {@link DisplayStatus#ENABLED} status are considered — these are
     * thoughts that have been approved by the AI moderator in the thoughts-evaluator service.</p>
     *
     * @return a rendered HTML page showing a random thought, or a placeholder if none exist
     */
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

    /**
     * Renders all approved thoughts as an HTML page.
     *
     * <p>Uses the Panache {@code find()} method with a field-level query to select only
     * thoughts with {@link DisplayStatus#ENABLED}, ensuring rejected or pending thoughts
     * are never shown to end users.</p>
     *
     * @return a rendered HTML page listing all approved thoughts
     */
    @GET
    @Path("/all")
    @Produces(MediaType.TEXT_HTML)
    public TemplateInstance getAllThoughts() {
        Log.debug("Retrieving all thoughts");
        return Templates.allThoughts(Thought.find("displayStatus", DisplayStatus.ENABLED).list());
    }
}
