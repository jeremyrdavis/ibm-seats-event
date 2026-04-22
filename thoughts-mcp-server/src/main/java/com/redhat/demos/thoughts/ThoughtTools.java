package com.redhat.demos.thoughts;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ThoughtTools {

    @Tool(description = "Retrieve all thoughts from the database")
    public String listThoughts() {
        List<Thought> thoughts = Thought.listAll();
        if (thoughts.isEmpty()) {
            return "No thoughts found in the database.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(thoughts.size()).append(" thoughts:\n\n");
        for (Thought t : thoughts) {
            formatThought(sb, t);
        }
        return sb.toString();
    }

    @Tool(description = "Search thoughts by keyword in their content")
    public String searchThoughtsByContent(
            @ToolArg(description = "The keyword or phrase to search for in thought content") String keyword) {
        List<Thought> thoughts = Thought.find("lower(content) like lower(?1)", "%" + keyword + "%").list();
        if (thoughts.isEmpty()) {
            return "No thoughts found matching keyword: " + keyword;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(thoughts.size()).append(" thoughts matching '").append(keyword).append("':\n\n");
        for (Thought t : thoughts) {
            formatThought(sb, t);
        }
        return sb.toString();
    }

    @Tool(description = "Get all thoughts by a specific author")
    public String getThoughtsByAuthor(
            @ToolArg(description = "The name of the author to filter by") String author) {
        List<Thought> thoughts = Thought.find("lower(author) like lower(?1)", "%" + author + "%").list();
        if (thoughts.isEmpty()) {
            return "No thoughts found by author: " + author;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Found ").append(thoughts.size()).append(" thoughts by '").append(author).append("':\n\n");
        for (Thought t : thoughts) {
            formatThought(sb, t);
        }
        return sb.toString();
    }

    @Tool(description = "Get a specific thought by its database ID")
    public String getThoughtById(
            @ToolArg(description = "The numeric ID of the thought to retrieve") Long id) {
        Thought thought = Thought.findById(id);
        if (thought == null) {
            return "No thought found with ID: " + id;
        }
        StringBuilder sb = new StringBuilder();
        formatThought(sb, thought);
        return sb.toString();
    }

    private void formatThought(StringBuilder sb, Thought t) {
        sb.append("--- Thought #").append(t.id).append(" ---\n");
        sb.append("Content: ").append(t.getContent()).append("\n");
        sb.append("Author: ").append(t.getAuthor()).append("\n");
        sb.append("Author Bio: ").append(t.getAuthorBio()).append("\n");
        sb.append("Status: ").append(t.displayStatus).append("\n\n");
    }
}
