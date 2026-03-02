package az.fitnest.user.dto.event;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

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
