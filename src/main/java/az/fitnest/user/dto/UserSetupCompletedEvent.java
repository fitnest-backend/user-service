package az.fitnest.user.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSetupCompletedEvent {
    private String eventId;
    private Long userId;
    private Long timestamp;
    private String source;
}
