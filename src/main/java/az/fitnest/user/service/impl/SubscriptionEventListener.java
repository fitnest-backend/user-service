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

    @KafkaListener(topics = "subscription-events", groupId = "user-service")
    public void handleSubscriptionEvent(Map<String, Object> event) {
        Long userId = (Long) event.get("userId");
        if (userId != null) {
            cacheManager.getCache("user_me").evict(userId);
        }
    }
}
