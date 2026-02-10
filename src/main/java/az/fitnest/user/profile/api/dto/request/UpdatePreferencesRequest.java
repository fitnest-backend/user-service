package az.fitnest.user.profile.api.dto.request;

import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    
    private String language;
    
    private String theme;
    
    private PushNotification notifications;
}
