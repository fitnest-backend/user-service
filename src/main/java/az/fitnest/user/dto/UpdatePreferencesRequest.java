package az.fitnest.user.dto;

import lombok.Data;

@Data
public class UpdatePreferencesRequest {
    
    private String language;
    
    private String theme;
    
    private PushNotification notifications;
}
