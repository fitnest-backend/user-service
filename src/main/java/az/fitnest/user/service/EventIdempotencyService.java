package az.fitnest.user.service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
@Service
@RequiredArgsConstructor
public class EventIdempotencyService {
    private static final String KEY_PREFIX = "event:processed:";
    private static final Duration TTL = Duration.ofHours(24);
    private final StringRedisTemplate stringRedisTemplate;
    public boolean markProcessedIfNew(String eventId) {
        if (eventId == null || eventId.isBlank()) return true; // process if no id
        String key = KEY_PREFIX + eventId;
        Boolean set = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", TTL);
        return Boolean.TRUE.equals(set);
    }
}
