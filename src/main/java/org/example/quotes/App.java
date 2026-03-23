package org.example.quotes;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.example.quotes.api.HealthController;
import org.example.quotes.api.QuoteController;
import org.example.quotes.config.AppConfig;
import org.example.quotes.core.QuoteRepository;
import org.example.quotes.core.QuoteService;
import org.example.quotes.util.JsonUtil;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.NoSuchElementException;

public class App {

    public static void main(String[] args) {
        AppConfig cfg = new AppConfig();

        // Logging baseline
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        // Metrics
        PrometheusMeterRegistry prometheus = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        bindSystemMetrics(prometheus);

        // Core components
        QuoteRepository repo = new QuoteRepository();
        if (cfg.getSeedFile() != null && !cfg.getSeedFile().isBlank()) {
            repo.seedFromFileIfPresent(cfg.getSeedFile());
        }
        if (repo.size() == 0) {
            repo.seedDefault();
        }
        QuoteService service = new QuoteService(repo);

        // Web app
        Javalin app = Javalin.create(conf -> {
            conf.showJavalinBanner = false;
            conf.http.defaultContentType = "application/json";
            // If you wish to configure CORS, place it here; keeping default for simplicity
        });

        // Basic access logging
        app.after(ctx -> {
            String method = ctx.method().name();
            String path = ctx.path();
            int status = ctx.status().getCode();
            LoggerFactory.getLogger("http").info("{} {} -> {}", method, path, status);
        });

        // Global exception handling
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            JsonUtil.badRequest(ctx, Map.of("error", e.getMessage()));
        });
        app.exception(NoSuchElementException.class, (e, ctx) -> {
            JsonUtil.notFound(ctx, Map.of("error", e.getMessage()));
        });
        app.exception(Exception.class, (e, ctx) -> {
            JsonUtil.error(ctx, HttpStatus.INTERNAL_SERVER_ERROR.getCode(), "internal_error");
        });

        // Controllers
        new QuoteController(service).register(app, cfg.getContextPath());
        new HealthController(cfg.getReadinessDelayMs()).register(app, cfg.getContextPath());

        // Metrics endpoint
        if (cfg.isPrometheusEnabled()) {
            String base = cfg.getContextPath().endsWith("/") ? cfg.getContextPath().substring(0, cfg.getContextPath().length() - 1) : cfg.getContextPath();
            app.get(base + "/metrics", ctx -> {
                ctx.contentType("text/plain; version=0.0.4; charset=utf-8");
                ctx.result(prometheus.scrape());
            });
        }

        // Start
        app.start(cfg.getPort());

        // Graceful shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
            } catch (Exception ignored) {
            }
        }));
    }

    private static void bindSystemMetrics(MeterRegistry registry) {
        new ClassLoaderMetrics().bindTo(registry);
        new JvmMemoryMetrics().bindTo(registry);
        new JvmGcMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new UptimeMetrics().bindTo(registry);
    }

}
