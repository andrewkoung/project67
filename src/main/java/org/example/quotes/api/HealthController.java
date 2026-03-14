package org.example.quotes.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.example.quotes.util.JsonUtil;

import java.time.Instant;
import java.util.Map;

public class HealthController {

    private final long notReadyUntilMillis;

    public HealthController(long readinessDelayMs) {
        this.notReadyUntilMillis = System.currentTimeMillis() + Math.max(0, readinessDelayMs);
    }

    public void register(Javalin app, String basePath) {
        String root = basePath.endsWith("/") ? basePath.substring(0, basePath.length() - 1) : basePath;
        app.get(root + "/health", this::health);
        app.get(root + "/ready", this::ready);
    }

    private void health(Context ctx) {
        JsonUtil.ok(ctx, Map.of(
                "status", "UP",
                "time", Instant.now().toString()
        ));
    }

    private void ready(Context ctx) {
        boolean ready = System.currentTimeMillis() >= notReadyUntilMillis;
        if (ready) {
            JsonUtil.ok(ctx, Map.of("status", "READY"));
        } else {
            JsonUtil.serviceUnavailable(ctx, Map.of("status", "NOT_READY", "retryAfterMs", notReadyUntilMillis - System.currentTimeMillis()));
        }
    }
}
