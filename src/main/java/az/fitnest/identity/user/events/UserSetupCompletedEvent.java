package az.fitnest.identity.user.events;
import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class UserSetupCompletedEvent {
    private String eventId;
    private Long userId;
    private Long timestamp;
    private String source;
}
