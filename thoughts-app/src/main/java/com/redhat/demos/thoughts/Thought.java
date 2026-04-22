package com.redhat.demos.thoughts;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * JPA entity representing a thought (quote or idea) submitted by a user.
 *
 * <p>Extends {@link PanacheEntity}, which is the Quarkus Panache "active record" base class.
 * It provides an auto-generated {@code id} field and convenience methods for persistence
 * operations (e.g. {@code persist()}, {@code listAll()}, {@code findById()}, {@code find()})
 * directly on the entity class, eliminating the need for a separate DAO or repository.</p>
 *
 * <p>{@link Entity @Entity} marks this class as a JPA entity, meaning Hibernate ORM will
 * map it to a database table. By default the table name matches the class name ("Thought").</p>
 */
@Entity
public class Thought extends PanacheEntity {

    /** The text content of the thought or quote. */
    private String content;

    /** The name of the person who authored or submitted the thought. */
    private String author;

    /** A short biographical description of the author. */
    private String authorBio;

    /**
     * The moderation status of this thought, controlling whether it is shown in the UI.
     *
     * <p>{@link Enumerated @Enumerated(EnumType.STRING)} tells JPA to store the enum value
     * as its name (e.g. "PENDING", "ENABLED", "DISABLED") rather than its ordinal integer.
     * This makes the database column human-readable and resilient to enum reordering.</p>
     */
    @Enumerated(EnumType.STRING)
    DisplayStatus displayStatus;

    /**
     * No-arg constructor required by JPA. Hibernate uses this to instantiate entities
     * when loading them from the database via reflection.
     */
    public Thought() {
    }

    /**
     * Creates a new Thought with the given content and author information.
     * The {@link #displayStatus} defaults to {@link DisplayStatus#PENDING},
     * meaning the thought must be approved by the evaluator before it is shown.
     *
     * @param content   the text of the thought
     * @param author    the author's name
     * @param authorBio a short bio of the author
     */
    public Thought(String content, String author, String authorBio) {
        this.content = content;
        this.author = author;
        this.authorBio = authorBio;
        this.displayStatus = DisplayStatus.PENDING;
    }

    @Override
    public String toString() {
        return "Thought{" +
                "content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", authorBio='" + authorBio + '\'' +
                ", id=" + id +
                '}';
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorBio() {
        return authorBio;
    }

    public void setAuthorBio(String authorBio) {
        this.authorBio = authorBio;
    }
}
