package az.fitnest.user.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Application warmup configuration that pre-warms various resources at startup.
 * Mirrors identity-service to eliminate cold-start latency.
 */
@Slf4j
@Configuration
public class ApplicationWarmupConfig {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.warmup.enabled:true}")
    private boolean warmupEnabled;

    @Value("${app.warmup.db:true}")
    private boolean warmupDb;

    public ApplicationWarmupConfig(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
    }

    /**
     * Warm up application resources after startup. Runs asynchronously so startup is not blocked.
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void warmupApplication() {
        if (!warmupEnabled) {
            return;
        }

        if (warmupDb) {
            warmupDatabase();
        }

        warmupRedis();
        warmupJit();
    }

    /**
     * Warm up database connection pool by executing a simple query several times to establish connections.
     */
    private void warmupDatabase() {
        try {
            long start = System.currentTimeMillis();
            for (int i = 0; i < 3; i++) {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        rs.getInt(1);
                    }
                }
            }
            log.debug("Database warmup completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("Failed to warm up database: {}", e.getMessage());
        }
    }

    /**
     * Warm up Redis connection by issuing a lightweight command.
     */
    private void warmupRedis() {
        try {
            log.debug("Warming up Redis connection...");
            long start = System.currentTimeMillis();
            redisTemplate.hasKey("__warmup__");
            log.debug("Redis warmup completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("Failed to warm up Redis: {}", e.getMessage());
        }
    }

    /**
     * Warm up JIT compiler by touching common code paths.
     */
    private void warmupJit() {
        try {
            log.debug("Warming up JIT...");
            long start = System.currentTimeMillis();

            String test = "warmup-test-string";
            test.toLowerCase();
            test.toUpperCase();
            test.split("-");

            java.util.List<String> list = new java.util.ArrayList<>();
            list.add("test");
            list.stream().filter(s -> s != null).count();

            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("key", "value");
            map.get("key");

            log.debug("JIT warmup completed in {}ms", System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.warn("Failed JIT warmup: {}", e.getMessage());
        }
    }
}

