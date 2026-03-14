package org.example.quotes.core;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class QuoteService {

    private final QuoteRepository repo;

    public QuoteService(QuoteRepository repo) {
        this.repo = repo;
    }

    public List<Quote> list(int page, int size) {
        if (page < 0 || size <= 0 || size > 500) {
            throw new IllegalArgumentException("Invalid pagination parameters");
        }
        return repo.findAll().stream()
                .sorted(Comparator.comparingLong(Quote::getId))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    public Quote get(long id) {
        return repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("Quote " + id + " not found"));
    }

    public Quote random() {
        return repo.random().orElseThrow(() ->
                new NoSuchElementException("No quotes available"));
    }

    public Quote create(String text, String author) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text is required");
        }
        if (author == null || author.isBlank()) {
            author = "Unknown";
        }
        return repo.add(new Quote(0, text.trim(), author.trim()));
    }

    public void delete(long id) {
        boolean removed = repo.delete(id);
        if (!removed) {
            throw new NoSuchElementException("Quote " + id + " not found");
        }
    }
}
