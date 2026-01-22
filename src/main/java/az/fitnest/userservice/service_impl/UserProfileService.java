package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.request.UpdateUserProfileRequest;
import az.fitnest.userservice.response.SummaryResponse;
import az.fitnest.userservice.response.UserProfileResponse;
import az.fitnest.userservice.service_inter.UserProfileInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService implements UserProfileInter{
	
	@Override
	public SummaryResponse getUserSummary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public UserProfileResponse getUserMe() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserMe(UpdateUserProfileRequest request) {
		// TODO Auto-generated method stub
		
	}

}
