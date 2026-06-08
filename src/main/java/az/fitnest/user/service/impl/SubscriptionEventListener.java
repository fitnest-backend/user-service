package az.fitnest.user.service.impl;
import java.util.Map;
import org.springframework.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionEventListener {
    @Autowired
    private CacheManager cacheManager;

    @KafkaListener(topics = "subscription-events", groupId = "user-backend")
    public void handleSubscriptionEvent(Map<String, Object> event) {
        Object userIdObj = event.get("userId");
        if (userIdObj != null) {
            Long userId = null;
            if (userIdObj instanceof Number) {
                userId = ((Number) userIdObj).longValue();
            } else if (userIdObj instanceof String) {
                try {
                    userId = Long.parseLong((String) userIdObj);
                } catch (NumberFormatException ignored) {}
            }
            if (userId != null) {
                org.springframework.cache.Cache userMeCache = cacheManager.getCache("user_me");
                if (userMeCache != null) {
                    userMeCache.evict(userId);
                }
                org.springframework.cache.Cache adminUsersCache = cacheManager.getCache("admin-users");
                if (adminUsersCache != null) {
                    adminUsersCache.clear();
                }
            }
        }
    }
}
