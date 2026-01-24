package az.fitnest.userservice.service;

import az.fitnest.userservice.dto.request.UpdatePreferences;

public interface PreferencesService {
	
	void updateMePreferences(UpdatePreferences request);

}
