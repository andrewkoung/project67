package org.example.quotes.core;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class QuoteRepository {

    private final ConcurrentHashMap<Long, Quote> store = new ConcurrentHashMap<>();
    private final AtomicLong idSeq = new AtomicLong(1);

    public QuoteRepository() {
    }

    public void seedDefault() {
        List<Quote> defaults = List.of(
                new Quote(0, "The only true wisdom is in knowing you know nothing.", "Socrates"),
                new Quote(0, "In the middle of difficulty lies opportunity.", "Albert Einstein"),
                new Quote(0, "Well begun is half done.", "Aristotle"),
                new Quote(0, "Knowing yourself is the beginning of all wisdom.", "Aristotle"),
                new Quote(0, "The unexamined life is not worth living.", "Socrates"),
                new Quote(0, "Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
                new Quote(0, "Do or do not. There is no try.", "Yoda"),
                new Quote(0, "Wisdom begins in wonder.", "Socrates")
        );
        defaults.forEach(q -> add(new Quote(0, q.getText(), q.getAuthor())));
    }

    public void seedFromFileIfPresent(String path) {
        if (path == null || path.isBlank()) return;
        try {
            File f = new File(path);
            if (!f.exists() || !f.isFile()) return;
            String content = Files.readString(f.toPath());
            ObjectMapper om = new ObjectMapper();
            List<Quote> items = om.readValue(content, new TypeReference<>() {});
            for (Quote q : items) {
                add(new Quote(0, q.getText(), q.getAuthor()));
            }
        } catch (Exception ignored) {
            // Best-effort seeding; ignore errors to keep app running
        }
    }

    public List<Quote> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(store.values()));
    }

    public Optional<Quote> findById(long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Quote add(Quote q) {
        long id = idSeq.getAndIncrement();
        Quote withId = q.withId(id);
        store.put(id, withId);
        return withId;
    }

    public boolean delete(long id) {
        return store.remove(id) != null;
    }

    public Optional<Quote> random() {
        List<Quote> all = findAll();
        if (all.isEmpty()) return Optional.empty();
        int idx = ThreadLocalRandom.current().nextInt(all.size());
        return Optional.of(all.get(idx));
    }

    public int size() {
        return store.size();
    }

    public void clear() {
        store.clear();
        idSeq.set(1);
    }
}
