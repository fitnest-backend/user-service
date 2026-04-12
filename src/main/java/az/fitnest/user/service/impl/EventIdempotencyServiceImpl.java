package az.fitnest.user.service.impl;

import az.fitnest.user.service.EventIdempotencyService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class EventIdempotencyServiceImpl implements EventIdempotencyService {
    private static final String KEY_PREFIX = "event:processed:";
    private final StringRedisTemplate stringRedisTemplate;
    private final MeterRegistry meterRegistry;

    @Value("${app.idempotency.ttl-hours:24}")
    private long ttlHours;

    private Duration ttl() {
        return Duration.ofHours(ttlHours);
    }

    @Override
    public boolean markProcessedIfNew(String eventId) {
        if (eventId == null || eventId.isBlank()) {
            meterRegistry.counter("idempotency.missing_id").increment();
            return true;
        }

        String normalized = eventId.trim();
        String id = DigestUtils.sha256Hex("user-backend:" + normalized);
        String key = KEY_PREFIX + id;

        try {
            Boolean set = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", ttl());
            boolean isNew = Boolean.TRUE.equals(set);
            meterRegistry.counter(isNew ? "idempotency.new" : "idempotency.duplicate").increment();
            return isNew;
        } catch (RedisConnectionFailureException | RedisSystemException | QueryTimeoutException e) {
            meterRegistry.counter("idempotency.redis_error").increment();
            return true;
        }
    }
}
