package az.fitnest.user.profile.api.dto.request;

import lombok.Data;

@Data
public class UpdatePreferences {
    
    private String language;
    
    private String theme;
    
    private PushNotification notifications;
}
