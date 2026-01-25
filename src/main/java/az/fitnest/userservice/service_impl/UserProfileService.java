package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.entity.UserProfile;
import az.fitnest.userservice.repository.UserProfileRepository;
import az.fitnest.userservice.request.UpdateUserProfileRequest;
import az.fitnest.userservice.response.SummaryResponse;
import az.fitnest.userservice.response.UserProfileResponse;
import az.fitnest.userservice.service_inter.UserProfileInter;
import az.fitnest.userservice.util.UserContextUtil;
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
		
		
		
	}

}
