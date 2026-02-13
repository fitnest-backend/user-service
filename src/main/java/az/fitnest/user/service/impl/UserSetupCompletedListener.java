package az.fitnest.user.service.impl;

import az.fitnest.user.dto.event.UserSetupCompletedEvent;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.service.EventIdempotencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSetupCompletedListener {
    private final UserProfileRepository userProfileRepository;
    private final EventIdempotencyService idempotencyService;

    @KafkaListener(topics = "user-setup-completed", groupId = "user-service", containerFactory = "userSetupCompletedKafkaListenerContainerFactory")
    public void onSetupCompleted(UserSetupCompletedEvent event) {
        if (event == null || event.getUserId() == null) return;
        if (!idempotencyService.markProcessedIfNew(event.getEventId())) return;
        Long userId = event.getUserId();
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });
        // No change in profile fields; this event may drive workflows. For now, we ensure profile exists.
        userProfileRepository.save(profile);
        // TODO: trigger workflows (plans ready) or mark local state if needed.
    }
}
