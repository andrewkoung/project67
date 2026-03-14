package org.example.quotes.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;

import java.util.Map;

public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .findAndRegisterModules()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    public static String toJson(Object o) {
        try {
            return MAPPER.writeValueAsString(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> type) {
        try {
            return MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T read(Context ctx, Class<T> type) {
        return fromJson(ctx.body(), type);
    }

    public static void ok(Context ctx, Object body) {
        ctx.status(200);
        ctx.contentType("application/json");
        ctx.result(toJson(body));
    }

    public static void created(Context ctx, Object body) {
        ctx.status(201);
        ctx.contentType("application/json");
        ctx.result(toJson(body));
    }

    public static void notFound(Context ctx, Object body) {
        ctx.status(404);
        ctx.contentType("application/json");
        ctx.result(toJson(body));
    }

    public static void badRequest(Context ctx, Object body) {
        ctx.status(400);
        ctx.contentType("application/json");
        ctx.result(toJson(body));
    }

    public static void serviceUnavailable(Context ctx, Object body) {
        ctx.status(503);
        ctx.contentType("application/json");
        ctx.result(toJson(body));
    }

    public static void error(Context ctx, int status, String message) {
        Map<String, Object> resp = Map.of("error", message);
        ctx.status(status);
        ctx.contentType("application/json");
        ctx.result(toJson(resp));
    }
}
