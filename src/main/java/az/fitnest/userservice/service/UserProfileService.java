package az.fitnest.userservice.service;

import az.fitnest.userservice.request.UpdateUserProfileRequest;
import az.fitnest.userservice.response.SummaryResponse;
import az.fitnest.userservice.response.UserProfileResponse;

public interface UserProfileService {
	
	SummaryResponse getUserSummary();
	
	UserProfileResponse getUserMe();
	
	void updateUserMe(UpdateUserProfileRequest request);

}
