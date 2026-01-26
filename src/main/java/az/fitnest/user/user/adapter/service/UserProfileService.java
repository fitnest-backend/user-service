package az.fitnest.user.user.adapter.service;

import org.springframework.stereotype.Service;

import az.fitnest.user.shared.exception.ConflictException;
import az.fitnest.user.shared.util.UserContextUtil;
import az.fitnest.user.user.adapter.client.IamServiceClient;
import az.fitnest.user.user.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.user.adapter.client.dto.UserResponse;
import az.fitnest.user.user.adapter.persistence.UserProfileRepository;
import az.fitnest.user.user.api.dto.request.DeleteAccountRequest;
import az.fitnest.user.user.api.dto.request.SetupRequest;
import az.fitnest.user.user.api.dto.response.ActiveSubscriptionResponse;
import az.fitnest.user.user.api.dto.response.SetupResponse;
import az.fitnest.user.user.api.dto.response.SummaryResponse;
import az.fitnest.user.user.api.dto.response.UserProfileResponse;
import az.fitnest.user.user.domain.enums.Gender;
import az.fitnest.user.user.domain.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final IamServiceClient iamServiceClient;
	private final UserProfileRepository userProfileRepository;
	
	public SummaryResponse getUserSummary() {
		return null;
	}

	public UserProfileResponse getUserMe() {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UserResponse iamUser = iamServiceClient.getUserById(userId);
		
		return UserProfileResponse.builder()
				.userId(iamUser.getUserId())
				.fullName(iamUser.getFullName())
				.mobile(iamUser.getMobile())
				.email(iamUser.getEmail())
				.profileImageUrl(iamUser.getProfileImageUrl())
				.createdAt(iamUser.getCreatedAt())
				.build();
	}

	public UserProfileResponse updateUserMe(az.fitnest.user.user.api.dto.request.UpdateUserProfileRequest request) {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest();
		updateRequest.setFullName(request.getFullName());
		updateRequest.setEmail(request.getEmail());
		
		UserResponse updatedUser = iamServiceClient.updateUserProfile(userId, updateRequest);
		
		return UserProfileResponse.builder()
				.userId(updatedUser.getUserId())
				.fullName(updatedUser.getFullName())
				.mobile(updatedUser.getMobile())
				.email(updatedUser.getEmail())
				.profileImageUrl(updatedUser.getProfileImageUrl())
				.createdAt(updatedUser.getCreatedAt())
				.build();
	}

	public UserProfileResponse updateProfileImage(String imageUrl) {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UpdateProfileImageRequest request = new UpdateProfileImageRequest();
		request.setImageUrl(imageUrl);
		
		UserResponse updatedUser = iamServiceClient.updateProfileImage(userId, request);
		
		return UserProfileResponse.builder()
				.userId(updatedUser.getUserId())
				.fullName(updatedUser.getFullName())
				.mobile(updatedUser.getMobile())
				.email(updatedUser.getEmail())
				.profileImageUrl(updatedUser.getProfileImageUrl())
				.createdAt(updatedUser.getCreatedAt())
				.build();
	}

	public ActiveSubscriptionResponse getActiveSubscription() {
		Long userId = UserContextUtil.getCurrentUserId();
		
		return ActiveSubscriptionResponse.builder()
				.status("none")
				.build();
	}

	public void deleteAccount(DeleteAccountRequest request) {
		if (!Boolean.TRUE.equals(request.getConfirm())) {
			throw new IllegalArgumentException("Confirmation must be true");
		}
		
		Long userId = UserContextUtil.getCurrentUserId();
		
		throw new ConflictException("Account deletion not yet implemented. Please contact support.");
	}

	@Transactional
	public SetupResponse setupProfile(SetupRequest request) {
		Long userId = UserContextUtil.getCurrentUserId();
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseGet(() -> {
					UserProfile newProfile = new UserProfile();
					newProfile.setUserId(userId);
					return newProfile;
				});
		
		if (request.getProfile() != null) {
			if (request.getProfile().getHeightCm() != null) {
				profile.setHeightCm(request.getProfile().getHeightCm());
			}
			if (request.getProfile().getWeightKg() != null) {
				profile.setWeightKg(request.getProfile().getWeightKg());
			}
			if (request.getProfile().getGender() != null) {
				try {
					profile.setGender(Gender.valueOf(request.getProfile().getGender().toUpperCase()));
				} catch (IllegalArgumentException e) {
					profile.setGender(null);
				}
			}
			if (request.getProfile().getBirthDate() != null) {
				profile.setBirthDate(LocalDate.parse(request.getProfile().getBirthDate(), DateTimeFormatter.ISO_DATE));
			}
			if (request.getProfile().getGoal() != null) {
				profile.setGoalCode(request.getProfile().getGoal());
			}
		}
		
		userProfileRepository.save(profile);
		
		az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest setupRequest =
				new az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest();
		setupRequest.setSetupRequired(false);
		iamServiceClient.updateSetupRequired(userId, setupRequest);
		
		UserResponse iamUser = iamServiceClient.getUserById(userId);
		
		SetupResponse.UserInfo.ProfileInfo profileInfo = SetupResponse.UserInfo.ProfileInfo.builder()
				.heightCm(profile.getHeightCm())
				.weightKg(profile.getWeightKg())
				.gender(profile.getGender() != null ? profile.getGender().name().toLowerCase() : null)
				.birthDate(profile.getBirthDate() != null ? profile.getBirthDate().format(DateTimeFormatter.ISO_DATE) : null)
				.goal(profile.getGoalCode())
				.build();
		
		SetupResponse.UserInfo userInfo = SetupResponse.UserInfo.builder()
				.userId(iamUser.getUserId())
				.language(request.getLanguage())
				.profile(profileInfo)
				.build();
		
		return SetupResponse.builder()
				.setupRequired(false)
				.user(userInfo)
				.build();
	}
	
}
