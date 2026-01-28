package az.fitnest.user.preferences.api.dto.request;

import az.fitnest.user.user.api.dto.request.PushNotification;
import lombok.Data;

@Data
public class UpdatePreferences {
	
	private String language;
	
	private String theme;
	
	private PushNotification notifications;

}
