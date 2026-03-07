package az.fitnest.user.service.impl;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserEventListener {

    @KafkaListener(topics = "user-events", groupId = "user-service")
    public void handleUserEvent(Map<String, Object> event) {
        String eventType = (String) event.get("eventType");
        Long userId = (Long) event.get("userId");

        if ("USER_UPDATED".equals(eventType)) {
        }
    }
}
