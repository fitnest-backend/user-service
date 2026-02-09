package az.fitnest.user.profile.api.dto.request;

import lombok.Data;

@Data
public class PushNotification {
    private boolean enabled;
    private boolean promotions;
    private boolean workoutReminders;
}
