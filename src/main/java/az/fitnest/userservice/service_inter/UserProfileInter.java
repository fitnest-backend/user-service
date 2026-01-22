package az.fitnest.userservice.service_inter;

import az.fitnest.userservice.request.UpdateUserProfileRequest;
import az.fitnest.userservice.response.SummaryResponse;
import az.fitnest.userservice.response.UserProfileResponse;

public interface UserProfileInter {
	
	
	SummaryResponse getUserSummary();
	
	UserProfileResponse getUserMe();
	
	void updateUserMe(UpdateUserProfileRequest request);

}
