package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.request.UpdatePreferences;
import az.fitnest.userservice.service_inter.PreferencesInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreferencesService implements PreferencesInter{
	
	@Override
	public void updateMePreferences(UpdatePreferences request) {
		// TODO Auto-generated method stub
		
	}

}
