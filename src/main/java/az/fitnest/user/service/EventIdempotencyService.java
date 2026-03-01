package az.fitnest.user.service;

import java.time.Duration;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

public interface EventIdempotencyService {
    boolean markProcessedIfNew(String eventId);
}
