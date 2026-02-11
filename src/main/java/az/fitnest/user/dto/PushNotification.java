package az.fitnest.user.dto;

import lombok.Data;

@Data
public class PushNotification {
    private boolean enabled;
    private boolean promotions;
    private boolean workoutReminders;
}
