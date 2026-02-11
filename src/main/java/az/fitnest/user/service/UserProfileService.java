package az.fitnest.user.service;

import az.fitnest.user.client.IdentityGrpcClient;
import az.fitnest.user.grpc.UserResponse;
import az.fitnest.user.service.FavoritesService;
import az.fitnest.user.constants.EntityType;
import az.fitnest.user.client.UpdateProfileImageRequest;
import az.fitnest.user.client.UpdateSetupRequiredRequest;
import az.fitnest.user.repository.UserLocationRepository;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.dto.*;
import az.fitnest.user.dto.*;
import az.fitnest.user.constants.Gender;
import az.fitnest.user.entity.UserLocation;
import az.fitnest.user.entity.UserProfile;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.criteria.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final IdentityGrpcClient identityServiceClient;
    private final UserProfileRepository userProfileRepository;
    private final FileStorageService fileStorageService;
    private final FavoritesService favoritesService;
    private final UserLocationRepository userLocationRepository;
    private final az.fitnest.user.repository.GoalReferenceRepository goalReferenceRepository;

    public SummaryResponse getUserSummary() {
        Long userId = UserContext.getCurrentUserId();
        UserResponse identityUser = identityServiceClient.getUserById(userId);

        UserProfileResponse user = UserProfileResponse.builder()
                .userId(String.valueOf(identityUser.getUserId()))
                .firstName(identityUser.getFirstName())
                .lastName(identityUser.getLastName())
                .profileImageUrl(identityUser.getProfileImageUrl())
                .build();

        CountersResponse counters = new CountersResponse();
        Map<EntityType, Long> favoriteCounts = favoritesService.getFavoriteCounts(userId);

        counters.setFavorite_gyms(favoriteCounts.getOrDefault(EntityType.GYM, 0L));
        counters.setFavorite_stores(favoriteCounts.getOrDefault(EntityType.STORE, 0L));

        SummaryResponse summary = new SummaryResponse();
        summary.setUser(user);
        summary.setCounters(counters);
        summary.setUnreadNotifications(0); // Placeholder
        return summary;
    }

    public UserProfileResponse getUserMe() {
        Long userId = UserContext.getCurrentUserId();
        UserResponse identityUser = identityServiceClient.getUserById(userId);

        return UserProfileResponse.builder()
                .userId(String.valueOf(identityUser.getUserId()))
                .firstName(identityUser.getFirstName())
                .lastName(identityUser.getLastName())
                .mobile(identityUser.getMobile())
                .email(identityUser.getEmail())
                .profileImageUrl(identityUser.getProfileImageUrl())
                .createdAt(parseCreatedAt(identityUser.getCreatedAt()))
                .build();
    }

    @Transactional
    public LocationResponse updateMyLocation(UpdateLocationRequest request) {
        Long userId = UserContext.getCurrentUserId();

        UserLocation location = userLocationRepository.findById(userId)
                .orElseGet(() -> new UserLocation(userId, request.getLat(), request.getLng(), LocalDateTime.now()));

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
    @CacheEvict(value = "user_profiles", key = "T(az.fitnest.user.criteria.UserContext).getCurrentUserId()")
    public void updateBody(UpdateBodyRequest request) {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        if (request.getHeightCm() != null) profile.setHeightCm(request.getHeightCm());
        if (request.getWeightKg() != null) profile.setWeightKg(request.getWeightKg());
        if (request.getGender() != null) profile.setGender(request.getGender());
        if (request.getBirthDate() != null) profile.setBirthDate(request.getBirthDate());

        userProfileRepository.save(profile);
    }

    public UserProfileResponse updateUserMe(UpdateUserProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();

        UserResponse updatedUser = identityServiceClient.updateUserProfile(userId, request.getFirstName(), request.getLastName(), request.getEmail());

        return UserProfileResponse.builder()
                .userId(String.valueOf(updatedUser.getUserId()))
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .mobile(updatedUser.getMobile())
                .email(updatedUser.getEmail())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .createdAt(parseCreatedAt(updatedUser.getCreatedAt()))
                .build();
    }

    public UserProfileResponse updateProfileImage(MultipartFile file) {
        Long userId = UserContext.getCurrentUserId();

        UserResponse currentUser = identityServiceClient.getUserById(userId);
        String oldImageUrl = currentUser.getProfileImageUrl();

        String newImageUrl = fileStorageService.saveFile(file);

        try {
            UserResponse updatedUser = identityServiceClient.updateProfileImage(userId, newImageUrl);

            return UserProfileResponse.builder()
                    .userId(String.valueOf(updatedUser.getUserId()))
                    .firstName(updatedUser.getFirstName())
                    .lastName(updatedUser.getLastName())
                    .mobile(updatedUser.getMobile())
                    .email(updatedUser.getEmail())
                    .profileImageUrl(updatedUser.getProfileImageUrl())
                    .createdAt(parseCreatedAt(updatedUser.getCreatedAt()))
                    .build();
        } finally {
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                fileStorageService.deleteFile(oldImageUrl);
            }
        }
    }

    public ActiveSubscriptionResponse getActiveSubscription() {
        return ActiveSubscriptionResponse.builder()
                .status("none")
                .build();
    }

    public void deleteAccount(DeleteAccountRequest request) {
        if (!Boolean.TRUE.equals(request.getConfirm())) {
            throw new BadRequestException("Confirmation must be true");
        }
        Long userId = UserContext.getCurrentUserId();
        identityServiceClient.deleteUser(userId, request.getReason());
    }

    @Transactional
    public void updateGoal(UpdateGoalsRequest request) {
        Long userId = UserContext.getCurrentUserId();

        if (!goalReferenceRepository.existsById(request.getGoalCode())) {
            throw new ResourceNotFoundException("Goal reference not found");
        }

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        profile.setGoalCode(request.getGoalCode());
        userProfileRepository.save(profile);
    }

    public void updatePreferences(UpdatePreferencesRequest request) {
        Long userId = UserContext.getCurrentUserId();
        
        if (request.getLanguage() != null) {
            identityServiceClient.updateLanguage(userId, request.getLanguage());
        }
        
        // Save theme/notifications if/when extended
    }

    @Transactional(readOnly = true)
    public GoalsResponse getReferenceGoals() {
        java.util.List<GoalItemResponse> items = goalReferenceRepository.findAllByOrderByGoalCodeAsc().stream()
                .map(goal -> GoalItemResponse.builder()
                        .code(goal.getGoalCode())
                        .title(goal.getTitle())
                        .subtitle(goal.getSubtitle())
                        .build())
                .toList();

        return GoalsResponse.builder()
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public SetupResponse getSetupStatus() {
        Long userId = UserContext.getCurrentUserId();

        UserResponse identityUser = identityServiceClient.getUserById(userId);
        UserProfile profile = userProfileRepository.findById(userId).orElse(new UserProfile());

        SetupResponse.ProfileData profileData = SetupResponse.ProfileData.builder()
                .heightCm(profile.getHeightCm())
                .weightKg(profile.getWeightKg())
                .gender(profile.getGender() != null ? profile.getGender().name().toLowerCase() : null)
                .age(calculateAge(profile.getBirthDate()))
                .build();

        return SetupResponse.builder()
                .setupRequired(identityUser.getSetupRequired())
                .profile(profileData)
                .goal(profile.getGoalCode())
                .build();
    }

    @Transactional(readOnly = true)
    public FitnessLevelResponse getFitnessLevel() {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getHeightCm() == null || profile.getWeightKg() == null) {
            throw new ConflictException("Profile incomplete"); // Using ConflictException for 409 as per old code logic
        }

        double heightM = profile.getHeightCm() / 100.0;
        double bmi = profile.getWeightKg() / (heightM * heightM);
        bmi = Math.round(bmi * 10.0) / 10.0;
        String category = getBmiCategory(bmi);

        FitnessLevelResponse.BmiScale bmiScale = FitnessLevelResponse.BmiScale.builder()
                .underweightMax(18.5)
                .normalMax(25.0)
                .overweightMax(30.0)
                .build();

        return FitnessLevelResponse.builder()
                .level("BEGINNER")
                .bmi(bmi)
                .bmiCategory(category)
                .bmiScale(bmiScale)
                .goal(profile.getGoalCode())
                .message(getBmiMessage(category))
                .build();
    }

    private String getBmiMessage(String category) {
        return switch (category) {
            case "UNDERWEIGHT" -> "Sizin çəkiniz normadan aşağıdır. Qidalanmanıza diqqət yetirin.";
            case "NORMAL" -> "Sizin çəkiniz normal diapazondadır. Belə davam edin!";
            case "OVERWEIGHT" -> "Sizin çəkiniz normadan artıqdır. Aktivliyinizi artırın.";
            case "OBESE" -> "Sizin çəkiniz piylənmə diapazonundadır. Mütəxəssislə məsləhətləşin.";
            default -> "Məlumat yoxdur.";
        };
    }

    @Transactional
    public CompleteSetupResponse completeSetup() {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getHeightCm() == null || profile.getWeightKg() == null || profile.getGoalCode() == null) {
            throw new ConflictException("Setup incomplete");
        }

        identityServiceClient.updateSetupRequired(userId, false);

        return CompleteSetupResponse.builder()
                .setupRequired(false)
                .next(CompleteSetupResponse.NextSteps.builder()
                        .workoutPlanReady(true)
                        .nutritionPlanReady(true)
                        .build())
                .build();
    }

    @Transactional
    @CacheEvict(value = "user_profiles", key = "T(az.fitnest.user.criteria.UserContext).getCurrentUserId()")
    public SetupResponse setupProfile(SetupRequest request) {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userId);
                    return newProfile;
                });

        if (request.getProfile() != null) {
            SetupRequest.ProfileInfo info = request.getProfile();
            if (info.getHeightCm() != null) profile.setHeightCm(info.getHeightCm());
            if (info.getWeightKg() != null) profile.setWeightKg(info.getWeightKg());
            if (info.getGender() != null) {
                try {
                    profile.setGender(Gender.valueOf(info.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    profile.setGender(null);
                }
            }
            if (info.getBirthDate() != null) {
                profile.setBirthDate(LocalDate.parse(info.getBirthDate(), DateTimeFormatter.ISO_DATE));
            }
            if (info.getGoal() != null) profile.setGoalCode(info.getGoal());
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

    private Integer calculateAge(LocalDate birthDate) {
        if (birthDate == null) return null;
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    private LocalDateTime parseCreatedAt(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(value);
    }

    private String getBmiCategory(double bmi) {
        if (bmi < 18.5) return "UNDERWEIGHT";
        if (bmi < 25) return "NORMAL";
        if (bmi < 30) return "OVERWEIGHT";
        return "OBESE";
    }
}
