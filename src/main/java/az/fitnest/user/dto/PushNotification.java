package az.fitnest.user.dto;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PushNotification {
    private boolean enabled;
    private boolean promotions;
    private boolean workoutReminders;

}
