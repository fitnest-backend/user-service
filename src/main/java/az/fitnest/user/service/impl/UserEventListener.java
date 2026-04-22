package az.fitnest.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventListener {

    private final CacheManager cacheManager;

    @KafkaListener(topics = "user-events", groupId = "user-backend")
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        Object userIdObj = event.get("userId");

        if ("USER_UPDATED".equals(eventType) && userIdObj != null) {
            Long userId = parseUserId(userIdObj);
            if (userId != null) {
                log.info("Received USER_UPDATED event for userId: {}. Evicting caches.", userId);
                
                evictCache("identity_users", userId);
                evictCache("user_summaries", userId);
                evictCache("user_me", userId);
            }
        }
    }

    private void evictCache(String cacheName, Long userId) {
        Optional.ofNullable(cacheManager.getCache(cacheName))
                .ifPresent(cache -> {
                    cache.evict(userId);
                    log.debug("Evicted userId: {} from cache: {}", userId, cacheName);
                });
    }

    private Long parseUserId(Object obj) {
        if (obj instanceof Number) {
            return ((Number) obj).longValue();
        } else if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                log.error("Failed to parse userId from string: {}", obj);
            }
        }
        return null;
    }
}
