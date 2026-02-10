package az.fitnest.user.adapter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserEventListener {

    private final CacheManager cacheManager;

    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        Long userId = (Long) event.get("userId");

        if ("USER_UPDATED".equals(eventType)) {
            // Clear user auth data cache
            cacheManager.getCache("userAuthData").evict(userId);

            log.info("Cleared user auth data cache for user {} due to {} event",
                    userId, eventType);
        }
    }
}
