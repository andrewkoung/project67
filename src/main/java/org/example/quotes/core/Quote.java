package org.example.quotes.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class Quote {
    private final long id;
    private final String text;
    private final String author;

    @JsonCreator
    public Quote(
            @JsonProperty("id") long id,
            @JsonProperty("text") String text,
            @JsonProperty("author") String author) {
        this.id = id;
        this.text = text;
        this.author = author;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getAuthor() {
        return author;
    }

    public Quote withId(long newId) {
        return new Quote(newId, this.text, this.author);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote)) return false;
        Quote quote = (Quote) o;
        return id == quote.id &&
                Objects.equals(text, quote.text) &&
                Objects.equals(author, quote.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, author);
    }
}
