package az.fitnest.user.user.adapter.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import az.fitnest.user.favorites.adapter.service.FavoritesService;
import az.fitnest.user.favorites.domain.enums.EntityType;
import az.fitnest.user.shared.exception.ConflictException;
import az.fitnest.user.shared.util.UserContext;
import az.fitnest.user.user.adapter.client.IamServiceClient;
import az.fitnest.user.user.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.user.adapter.client.dto.UserResponse;
import az.fitnest.user.user.adapter.persistence.UserLocationRepository;
import az.fitnest.user.user.adapter.persistence.UserProfileRepository;
import az.fitnest.user.user.api.dto.request.DeleteAccountRequest;
import az.fitnest.user.user.api.dto.request.SetupRequest;
import az.fitnest.user.user.api.dto.request.UpdateLocationRequest;
import az.fitnest.user.user.api.dto.response.ActiveSubscriptionResponse;
import az.fitnest.user.user.api.dto.response.CountersResponse;
import az.fitnest.user.user.api.dto.response.LocationResponse;
import az.fitnest.user.user.api.dto.response.SetupResponse;
import az.fitnest.user.user.api.dto.response.SummaryResponse;
import az.fitnest.user.user.api.dto.response.UserProfileResponse;
import az.fitnest.user.user.domain.enums.Gender;
import az.fitnest.user.user.domain.model.UserLocation;
import az.fitnest.user.user.domain.model.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UserProfileService {

	private final IamServiceClient iamServiceClient;
	private final UserProfileRepository userProfileRepository;
	private final FileStorageService fileStorageService;
	private final FavoritesService favoritesService;
	private final UserLocationRepository userLocationRepository;
	
	public SummaryResponse getUserSummary() {
		Long userId = UserContext.getCurrentUserId();
		UserResponse iamUser = iamServiceClient.getUserById(userId);
		
		UserProfileResponse user = UserProfileResponse.builder()
				.userId(iamUser.getUserId())
				.firstName(iamUser.getFirstName())
				.lastName(iamUser.getLastName())
				.profileImageUrl(iamUser.getProfileImageUrl())
				.build();
		
		CountersResponse counters = new CountersResponse();
		counters.setFavorite_gyms(favoritesService.countFavorites(userId, EntityType.GYM));
		counters.setFavorite_stores(favoritesService.countFavorites(userId, EntityType.STORE));
		
		SummaryResponse summary = new SummaryResponse();
		summary.setUser(user);
		summary.setCounters(counters);
		summary.setUnreadNotifications(0);
		return summary;
	}

	public UserProfileResponse getUserMe() {
		Long userId = UserContext.getCurrentUserId();
		
		UserResponse iamUser = iamServiceClient.getUserById(userId);
		
		return UserProfileResponse.builder()
				.userId(iamUser.getUserId())
				.firstName(iamUser.getFirstName())
				.lastName(iamUser.getLastName())
				.mobile(iamUser.getMobile())
				.email(iamUser.getEmail())
				.profileImageUrl(iamUser.getProfileImageUrl())
				.createdAt(iamUser.getCreatedAt())
				.build();
	}
	
	@Transactional
	public LocationResponse updateMyLocation(UpdateLocationRequest request) {
		Long userId = UserContext.getCurrentUserId();
		
		UserLocation location = userLocationRepository.findById(userId)
				.orElseGet(() -> new UserLocation(userId, 0.0, 0.0, LocalDateTime.now()));
		
		location.setLat(request.getLat());
		location.setLng(request.getLng());
		location.setUpdatedAt(LocalDateTime.now());
		
		UserLocation saved = userLocationRepository.save(location);
		
		return LocationResponse.builder()
				.lat(saved.getLat())
				.lng(saved.getLng())
				.updatedAt(saved.getUpdatedAt())
				.build();
	}

	@Transactional
	public void updateBody(az.fitnest.user.user.api.dto.request.UpdateBodyRequest request) {
		Long userId = UserContext.getCurrentUserId();
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseGet(() -> {
					UserProfile newProfile = new UserProfile();
					newProfile.setUserId(userId);
					return newProfile;
				});

		if (request.getHeightCm() != null) {
			profile.setHeightCm(request.getHeightCm());
		}
		if (request.getWeightKg() != null) {
			profile.setWeightKg(request.getWeightKg());
		}
		if (request.getGender() != null) {
			profile.setGender(request.getGender());
		}
		if (request.getBirthDate() != null) {
			profile.setBirthDate(request.getBirthDate());
		}
		
		userProfileRepository.save(profile);
	}

	public UserProfileResponse updateUserMe(az.fitnest.user.user.api.dto.request.UpdateUserProfileRequest request) {
		Long userId = UserContext.getCurrentUserId();
		
		UpdateUserProfileRequest updateRequest = new UpdateUserProfileRequest();
		updateRequest.setFirstName(request.getFirstName());
		updateRequest.setLastName(request.getLastName());
		updateRequest.setEmail(request.getEmail());
		
		UserResponse updatedUser = iamServiceClient.updateUserProfile(userId, updateRequest);
		
		return UserProfileResponse.builder()
				.userId(updatedUser.getUserId())
				.firstName(updatedUser.getFirstName())
				.lastName(updatedUser.getLastName())
				.mobile(updatedUser.getMobile())
				.email(updatedUser.getEmail())
				.profileImageUrl(updatedUser.getProfileImageUrl())
				.createdAt(updatedUser.getCreatedAt())
				.build();
	}

	public UserProfileResponse updateProfileImage(MultipartFile file) {
		Long userId = UserContext.getCurrentUserId();
		
		UserResponse currentUser = iamServiceClient.getUserById(userId);
		String oldImageUrl = currentUser.getProfileImageUrl();
		
		String newImageUrl = fileStorageService.saveFile(file);
		
		try {
			UpdateProfileImageRequest request = new UpdateProfileImageRequest();
			request.setImageUrl(newImageUrl);
			
			UserResponse updatedUser = iamServiceClient.updateProfileImage(userId, request);
			
			if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
				try {
					fileStorageService.deleteFile(oldImageUrl);
				} catch (Exception e) {
				}
			}
			
			return UserProfileResponse.builder()
					.userId(updatedUser.getUserId())
					.firstName(updatedUser.getFirstName())
					.lastName(updatedUser.getLastName())
					.mobile(updatedUser.getMobile())
					.email(updatedUser.getEmail())
					.profileImageUrl(updatedUser.getProfileImageUrl())
					.createdAt(updatedUser.getCreatedAt())
					.build();
		} catch (Exception e) {
			try {
				fileStorageService.deleteFile(newImageUrl);
			} catch (Exception deleteException) {
			}
			throw e;
		}
	}

	public UserProfileResponse updateProfileImage(String imageUrl) {
		Long userId = UserContext.getCurrentUserId();
		
		UserResponse currentUser = iamServiceClient.getUserById(userId);
		String oldImageUrl = currentUser.getProfileImageUrl();
		
		UpdateProfileImageRequest request = new UpdateProfileImageRequest();
		request.setImageUrl(imageUrl);
		
		UserResponse updatedUser = iamServiceClient.updateProfileImage(userId, request);
		
		if (oldImageUrl != null && !oldImageUrl.isEmpty() && !oldImageUrl.equals(imageUrl)) {
			try {
				fileStorageService.deleteFile(oldImageUrl);
			} catch (Exception e) {
			}
		}
		
		return UserProfileResponse.builder()
				.userId(updatedUser.getUserId())
				.firstName(updatedUser.getFirstName())
				.lastName(updatedUser.getLastName())
				.mobile(updatedUser.getMobile())
				.email(updatedUser.getEmail())
				.profileImageUrl(updatedUser.getProfileImageUrl())
				.createdAt(updatedUser.getCreatedAt())
				.build();
	}

	public ActiveSubscriptionResponse getActiveSubscription() {
		Long userId = UserContext.getCurrentUserId();
		
		return ActiveSubscriptionResponse.builder()
				.status("none")
				.build();
	}

	public void deleteAccount(DeleteAccountRequest request) {
		if (!Boolean.TRUE.equals(request.getConfirm())) {
			throw new az.fitnest.user.shared.exception.BadRequestException("Confirmation must be true");
		}

		Long userId = UserContext.getCurrentUserId();

		// TODO: check active subscription when subscriptions-service API is available.
		// If active subscription exists, throw ConflictException with code HAS_ACTIVE_SUBSCRIPTION.

		// Delegate soft delete + anonymization to IAM service.
		// Reason is optional and currently not persisted; passed for future audit/logging.
		iamServiceClient.deleteUser(userId, request.getReason());
	}

    @Transactional(readOnly = true)
    public SetupResponse getSetupStatus() {
        Long userId = UserContext.getCurrentUserId();
        
        UserResponse iamUser = iamServiceClient.getUserById(userId);
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElse(new UserProfile());
        
        SetupResponse.ProfileData profileData = SetupResponse.ProfileData.builder()
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .gender(profile.getGender() != null ? profile.getGender().name().toLowerCase() : null)
                .age(calculateAge(profile.getBirthDate()))
                .build();
        
        return SetupResponse.builder()
                .setupRequired(iamUser.getSetupRequired())
                .profile(profileData)
                .goal(profile.getGoalCode())
                .build();
    }

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            return null;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

	@Transactional(readOnly = true)
	public az.fitnest.user.user.api.dto.response.FitnessLevelResponse getFitnessLevel() {
		Long userId = UserContext.getCurrentUserId();
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new az.fitnest.user.shared.exception.ResourceNotFoundException("Profile not found"));

		if (profile.getHeightCm() == null || profile.getWeightKg() == null) {
			throw new az.fitnest.user.shared.exception.ConflictException("Profile incomplete");
		}
		
		double heightM = profile.getHeightCm() / 100.0;
		double bmi = profile.getWeightKg() / (heightM * heightM);
		String category = getBmiCategory(bmi);
		
		return az.fitnest.user.user.api.dto.response.FitnessLevelResponse.builder()
				.level("BEGINNER") // Default logic for now
				.bmi(Math.round(bmi * 10.0) / 10.0)
				.bmiCategory(category)
				.build();
	}

	private String getBmiCategory(double bmi) {
		if (bmi < 18.5) return "UNDERWEIGHT";
		if (bmi < 25) return "NORMAL";
		if (bmi < 30) return "OVERWEIGHT";
		return "OBESE";
	}

	@Transactional
	public az.fitnest.user.user.api.dto.response.CompleteSetupResponse completeSetup() {
		Long userId = UserContext.getCurrentUserId();
		
		UserProfile profile = userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new az.fitnest.user.shared.exception.ResourceNotFoundException("Profile not found"));
		
		if (profile.getHeightCm() == null || profile.getWeightKg() == null || profile.getGoalCode() == null) {
			throw new az.fitnest.user.shared.exception.ConflictException("Setup incomplete");
		}
		
		az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest setupRequest =
				new az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest();
		setupRequest.setSetupRequired(false);
		
		iamServiceClient.updateSetupRequired(userId, setupRequest);
		
		return az.fitnest.user.user.api.dto.response.CompleteSetupResponse.builder()
				.setupRequired(false)
				.next(az.fitnest.user.user.api.dto.response.CompleteSetupResponse.NextSteps.builder()
						.workoutPlanReady(true)
						.nutritionPlanReady(true)
						.build())
				.build();
	}
	
	@Transactional
	public SetupResponse setupProfile(SetupRequest request) {
		Long userId = UserContext.getCurrentUserId();
		
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
		
		SetupResponse.ProfileData profileData = SetupResponse.ProfileData.builder()
				.heightCm(profile.getHeightCm())
				.weightKg(profile.getWeightKg())
				.gender(profile.getGender() != null ? profile.getGender().name().toLowerCase() : null)
				.age(calculateAge(profile.getBirthDate()))
				.build();
		
		return SetupResponse.builder()
				.setupRequired(false)
				.profile(profileData)
				.goal(profile.getGoalCode())
				.build();
	}
	
}
