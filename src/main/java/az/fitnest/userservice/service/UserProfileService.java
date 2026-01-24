package az.fitnest.userservice.service;

import az.fitnest.userservice.dto.request.UpdateUserProfileRequest;
import az.fitnest.userservice.dto.response.SummaryResponse;
import az.fitnest.userservice.dto.response.UserProfileResponse;

public interface UserProfileService {
	
	SummaryResponse getUserSummary();
	
	UserProfileResponse getUserMe();
	
	void updateUserMe(UpdateUserProfileRequest request);

}
