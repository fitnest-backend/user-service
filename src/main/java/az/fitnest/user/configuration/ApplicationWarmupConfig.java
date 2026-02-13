package az.fitnest.user.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(prefix = "app.warmup", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ApplicationWarmupConfig {

    private final DataSource dataSource;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.warmup.db:true}")
    private boolean warmupDb;

    public ApplicationWarmupConfig(DataSource dataSource, RedisTemplate<String, Object> redisTemplate) {
        this.dataSource = dataSource;
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Async("warmupExecutor")
    public void warmupApplication() {
        if (warmupDb) {
            warmupDatabase();
        }

        warmupRedis();
    }

    private void warmupDatabase() {
        try {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT 1");
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    rs.getInt(1);
                }
            }
        } catch (Exception e) {
        }
    }

    private void warmupRedis() {
        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
        } catch (Exception e) {
        }
    }

    @Bean(name = "warmupExecutor")
    public Executor warmupExecutor() {
        return Executors.newSingleThreadExecutor();
    }
}
