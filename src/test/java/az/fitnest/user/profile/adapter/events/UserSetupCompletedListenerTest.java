package az.fitnest.user.profile.adapter.events;
import az.fitnest.user.dto.UserSetupCompletedEvent;
import az.fitnest.user.profile.adapter.persistence.UserProfileRepository;
import az.fitnest.user.profile.domain.model.UserProfile;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.*;
public class UserSetupCompletedListenerTest {
    @Test
    void shouldCreateProfileIfMissingOnSetupCompleted() {
        UserProfileRepository repo = mock(UserProfileRepository.class);
        EventIdempotencyService idempotencyService = mock(EventIdempotencyService.class);
        when(idempotencyService.markProcessedIfNew("evt-1")).thenReturn(true);
        when(repo.findById(1L)).thenReturn(java.util.Optional.empty());
        UserSetupCompletedListener listener = new UserSetupCompletedListener(repo, idempotencyService);
        UserSetupCompletedEvent event = UserSetupCompletedEvent.builder()
                .eventId("evt-1")
                .userId(1L)
                .timestamp(System.currentTimeMillis())
                .source("identity-service")
                .build();
        listener.onSetupCompleted(event);
        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(repo).save(captor.capture());
        UserProfile saved = captor.getValue();
        assert saved.getUserId().equals(1L);
    }
    @Test
    void shouldIgnoreNullEvent() {
        UserProfileRepository repo = mock(UserProfileRepository.class);
        EventIdempotencyService idempotencyService = mock(EventIdempotencyService.class);
        UserSetupCompletedListener listener = new UserSetupCompletedListener(repo, idempotencyService);
        listener.onSetupCompleted(null);
        verify(repo, never()).save(any());
    }
}
