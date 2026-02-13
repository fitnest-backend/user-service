package az.fitnest.user.dto.request;

import az.fitnest.user.dto.PushNotification;
import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    
    private String theme;
    
    private PushNotification notifications;
}
