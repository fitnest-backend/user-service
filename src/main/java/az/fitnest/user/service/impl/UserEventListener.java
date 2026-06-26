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
    private final az.fitnest.user.repository.UserProfileRepository userProfileRepository;
    private final az.fitnest.user.repository.UserLocationRepository userLocationRepository;
    private final az.fitnest.user.repository.RecentSearchRepository recentSearchRepository;

    @KafkaListener(topics = "user-events", groupId = "user-backend")
    @org.springframework.transaction.annotation.Transactional
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        Object userIdObj = event.get("userId");

        if (userIdObj != null) {
            Long userId = parseUserId(userIdObj);
            if (userId != null) {
                if ("USER_UPDATED".equals(eventType)) {
                    log.info("Received USER_UPDATED event for userId: {}. Evicting caches.", userId);
                    evictCaches(userId);
                } else if ("USER_HARD_DELETED".equals(eventType)) {
                    log.warn("Received USER_HARD_DELETED event for userId: {}. Deleting user data and evicting caches.", userId);
                    
                    recentSearchRepository.deleteByUserId(userId);
                    userLocationRepository.deleteById(userId);
                    userProfileRepository.deleteById(userId);
                    
                    evictCaches(userId);
                }
            }
        }
    }

    private void evictCaches(Long userId) {
        evictCache("identity_users", userId);
        evictCache("user_summaries", userId);
        evictCache("user_me", userId);
        Optional.ofNullable(cacheManager.getCache("admin-users"))
                .ifPresent(org.springframework.cache.Cache::clear);
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
