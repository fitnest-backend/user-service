package az.fitnest.user.user.adapter.service;

import org.springframework.stereotype.Service;

import az.fitnest.user.shared.exception.ResourceNotFoundException;
import az.fitnest.user.shared.util.UserContextUtil;
import az.fitnest.user.user.adapter.client.IamServiceClient;
import az.fitnest.user.user.adapter.client.dto.UserResponse;
import az.fitnest.user.user.adapter.persistence.UserProfileRepository;
import az.fitnest.user.user.api.dto.request.UpdateUserProfileRequest;
import az.fitnest.user.user.api.dto.response.SummaryResponse;
import az.fitnest.user.user.api.dto.response.UserProfileResponse;
import az.fitnest.user.user.domain.model.UserProfile;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final UserProfileRepository userProfileRepository;
	private final IamServiceClient iamServiceClient;
	
	public SummaryResponse getUserSummary() {
		return null;
	}

	public UserProfileResponse getUserMe() {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UserResponse iamUser = iamServiceClient.getUserById(userId);
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElse(null);
		
		UserProfileResponse response = new UserProfileResponse();
		response.setUserId(Long.parseLong(iamUser.getUserId()));
		response.setFullName(iamUser.getFullName());
		response.setSetupRequired(iamUser.getSetupRequired() != null ? iamUser.getSetupRequired() : false);
		response.setProfileImageUrl(iamUser.getProfileImageUrl());
		
		return response;
	}

	public void updateUserMe(UpdateUserProfileRequest request) {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseGet(() -> {
					UserProfile newProfile = new UserProfile();
					newProfile.setUserId(userId);
					return newProfile;
				});
		
		userProfileRepository.save(profile);
	}
	
}
