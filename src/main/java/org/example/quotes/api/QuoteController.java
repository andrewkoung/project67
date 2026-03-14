package org.example.quotes.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.quotes.core.Quote;
import org.example.quotes.core.QuoteService;
import org.example.quotes.util.JsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuoteController {

    private final QuoteService service;

    public QuoteController(QuoteService service) {
        this.service = service;
    }

    public void register(Javalin app, String basePath) {
        String root = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        String api = root + "/api/v1/quotes";

        app.get(api, this::list);
        app.get(api + "/random", this::random);
        app.get(api + "/{id}", this::getById);
        app.post(api, this::create);
        app.delete(api + "/{id}", this::deleteById);
    }

    private void list(Context ctx) {
        int page = parseInt(ctx.queryParam("page"), 0);
        int size = parseInt(ctx.queryParam("size"), 20);
        List<Quote> items = service.list(page, size);
        Map<String, Object> resp = new HashMap<>();
        resp.put("page", page);
        resp.put("size", size);
        resp.put("count", items.size());
        resp.put("data", items);
        JsonUtil.ok(ctx, resp);
    }

    private void random(Context ctx) {
        Quote q = service.random();
        JsonUtil.ok(ctx, q);
    }

    private void getById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        Quote q = service.get(id);
        JsonUtil.ok(ctx, q);
    }

    private void create(Context ctx) {
        CreateRequest req = JsonUtil.read(ctx, CreateRequest.class);
        Quote q = service.create(req.text, req.author);
        JsonUtil.created(ctx, q);
    }

    private void deleteById(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        service.delete(id);
        ctx.status(204);
    }

    private int parseInt(String s, int def) {
        try {
            return s == null ? def : Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    public static class CreateRequest {
        public String text;
        public String author;

        public CreateRequest() {}
        public CreateRequest(String text, String author) {
            this.text = text;
            this.author = author;
        }
    }
}
