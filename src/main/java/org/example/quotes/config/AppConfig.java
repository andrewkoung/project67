package org.example.quotes.config;

import java.util.Optional;

public class AppConfig {

    private final int port;
    private final String contextPath;
    private final boolean prometheusEnabled;
    private final long readinessDelayMs;
    private final String corsOrigins;
    private final String seedFile;

    public AppConfig() {
        this.port = getIntEnv("APP_PORT", 8080);
        this.contextPath = getEnv("APP_CONTEXT_PATH", "/");
        this.prometheusEnabled = getBoolEnv("ENABLE_PROMETHEUS", true);
        this.readinessDelayMs = getLongEnv("STARTUP_READY_DELAY_MS", 0L);
        this.corsOrigins = getEnv("APP_CORS_ORIGINS", "*");
        this.seedFile = getEnv("QUOTES_SEED_FILE", "");
    }

    public int getPort() {
        return port;
    }

    public String getContextPath() {
        return contextPath;
    }

    public boolean isPrometheusEnabled() {
        return prometheusEnabled;
    }

    public long getReadinessDelayMs() {
        return readinessDelayMs;
    }

    public String getCorsOrigins() {
        return corsOrigins;
    }

    public String getSeedFile() {
        return seedFile;
    }

    private static String getEnv(String key, String def) {
        return Optional.ofNullable(System.getenv(key))
                .orElseGet(() -> System.getProperty(key, def));
    }

    private static int getIntEnv(String key, int def) {
        try {
            return Integer.parseInt(getEnv(key, String.valueOf(def)));
        } catch (Exception e) {
            return def;
        }
    }

    private static long getLongEnv(String key, long def) {
        try {
            return Long.parseLong(getEnv(key, String.valueOf(def)));
        } catch (Exception e) {
            return def;
        }
    }

    private static boolean getBoolEnv(String key, boolean def) {
        String v = getEnv(key, String.valueOf(def));
        return v.equalsIgnoreCase("true") || v.equalsIgnoreCase("1") || v.equalsIgnoreCase("yes");
    }
}
