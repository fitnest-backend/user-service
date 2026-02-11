package az.fitnest.user.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
public class UserEventListener {


    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        Long userId = (Long) event.get("userId");

        if ("USER_UPDATED".equals(eventType)) {
            log.info("Received user update event for user {} with type {}",
                    userId, eventType);
            // TODO: Implement cache eviction for domain-level DTOs when caching is added at service layer
        }
    }
}
