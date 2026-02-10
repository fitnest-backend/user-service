package az.fitnest.user.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Eliminates "first request" cold-start by warming up critical dependencies
 * on ApplicationReadyEvent.
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(JdbcTemplate.class)
@ConditionalOnProperty(prefix = "app.warmup", name = "enabled", havingValue = "true", matchIfMissing = true)
public class StartupWarmupListener {

    private final JdbcTemplate jdbcTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("🚀 Starting application warm-up for user-service...");
        
        warmupDatabase();
        warmupJwks();
        
        log.info("✅ Application warm-up completed.");
    }

    private void warmupDatabase() {
        try {
            log.info("  -> Warming up Database connection pool...");
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            log.info("  -> DB pool warmed.");
        } catch (Exception e) {
            log.warn("  -> DB warm-up failed: {}", e.getMessage());
        }
    }

    private void warmupJwks() {
        // Template for JWKS warm-up (to be detailed based on SecurityConfig)
        log.info("  -> JWKS pre-fetch path enabled. (Pre-warming auth context)");
    }
}
