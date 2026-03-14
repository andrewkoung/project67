package org.example.quotes;

import org.example.quotes.core.Quote;
import org.example.quotes.core.QuoteRepository;
import org.example.quotes.core.QuoteService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuoteServiceTest {

    @Test
    void randomReturnsAQuoteWhenSeeded() {
        QuoteRepository repo = new QuoteRepository();
        repo.seedDefault();
        QuoteService svc = new QuoteService(repo);

        Quote q = svc.random();
        assertNotNull(q);
        assertTrue(q.getText().length() > 0);
    }

    @Test
    void paginationWorks() {
        QuoteRepository repo = new QuoteRepository();
        repo.seedDefault();
        QuoteService svc = new QuoteService(repo);

        assertEquals(4, svc.list(0, 4).size());
        assertEquals(4, svc.list(1, 4).size());
        assertTrue(svc.list(2, 4).size() >= 0);
    }
}
