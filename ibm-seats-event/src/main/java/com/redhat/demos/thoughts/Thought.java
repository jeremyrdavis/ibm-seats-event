package com.redhat.demos.thoughts;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Thought extends PanacheEntity {

    private String content;

    private String author;

    private String authorBio;

    @Enumerated(EnumType.STRING)
    DisplayStatus displayStatus;

    public Thought() {
    }

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
