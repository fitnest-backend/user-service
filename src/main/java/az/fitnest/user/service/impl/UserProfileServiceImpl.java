package az.fitnest.user.service.impl;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.dto.request.*;
import az.fitnest.user.dto.response.*;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.model.entity.UserLocation;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.model.enums.EntityType;
import az.fitnest.user.model.enums.Gender;
import az.fitnest.user.repository.LanguageRepository;
import az.fitnest.user.repository.UserLocationRepository;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.service.*;
import az.fitnest.user.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final CachedIdentityGrpcClient cachedIdentityClient;
    private final UserProfileRepository userProfileRepository;
    private final FileStorageService fileStorageService;
    private final FavoritesService favoritesService;
    private final UserLocationRepository userLocationRepository;
    private final az.fitnest.user.repository.GoalReferenceRepository goalReferenceRepository;
    private final LanguageRepository languageRepository;
    private final TranslationService translationService;

    private Long currentUserId() {
        return UserContext.getCurrentUserId();
    }

    @Cacheable(cacheNames = "user_summaries", key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", sync = true)
    // Dedicated cache for SummaryResponse to prevent type collisions with identity_users
    @Transactional(readOnly = true)
    @Override
    public SummaryResponse getUserSummary() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);

        UserProfileResponse user = mapToUserProfileResponse(identityUser);

        Map<EntityType, Long> favoriteCounts = favoritesService.getFavoriteCounts(userId);

        CountersResponse counters = new CountersResponse();
        counters.setFavorite_gyms(favoriteCounts.getOrDefault(EntityType.GYM, 0L));
        counters.setFavorite_stores(favoriteCounts.getOrDefault(EntityType.STORE, 0L));

        SummaryResponse summary = new SummaryResponse();
        summary.setUser(user);
        summary.setCounters(counters);
        summary.setUnreadNotifications(0); // placeholder
        return summary;
    }

    @Cacheable(cacheNames = "user_me", key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()")
    // Dedicated cache for UserProfileResponse to prevent type collisions with identity_users
    @Override
    public UserProfileResponse getUserMe() {
        Long userId = UserContext.getCurrentUserId();
        return mapToUserProfileResponse(cachedIdentityClient.getUserById(userId));
    }

    @Transactional
    @Override
    public LocationResponse updateMyLocation(UpdateLocationRequest request) {
        Long userId = UserContext.getCurrentUserId();

        var existingOpt = userLocationRepository.findById(userId);
        UserLocation location = existingOpt.orElseGet(() ->
                new UserLocation(userId, request.getLat(), request.getLng(), LocalDateTime.now())
        );

        boolean isNew = existingOpt.isEmpty();
        boolean latChanged = !Objects.equals(location.getLat(), request.getLat());
        boolean lngChanged = !Objects.equals(location.getLng(), request.getLng());

        if (isNew || latChanged || lngChanged) {
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

        return LocationResponse.builder()
                .lat(location.getLat())
                .lng(location.getLng())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    @CacheEvict(cacheNames = {"user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public void updateBody(UpdateBodyRequest request) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = getOrCreateProfile(userId);

        boolean dirty = false;

        if (request.getHeightCm() != null) {
            Double newHeight = request.getHeightCm().doubleValue();
            if (!Objects.equals(profile.getHeightCm(), newHeight)) {
                profile.setHeightCm(newHeight);
                dirty = true;
            }
        }
        if (request.getWeightKg() != null) {
            if (!Objects.equals(profile.getWeightKg(), request.getWeightKg())) {
                profile.setWeightKg(request.getWeightKg());
                dirty = true;
            }
        }
        if (request.getGender() != null) {
            if (!Objects.equals(profile.getGender(), request.getGender())) {
                profile.setGender(request.getGender());
                dirty = true;
            }
        }
        if (request.getBirthDate() != null) {
            if (!Objects.equals(profile.getBirthDate(), request.getBirthDate())) {
                profile.setBirthDate(request.getBirthDate());
                dirty = true;
            }
        }

        if (dirty) {
            userProfileRepository.save(profile);
        }
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public UserProfileResponse updateUserMe(UpdateUserProfileRequest request) {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse updated = cachedIdentityClient.updateUserProfile(
                userId, request.getFirstName(), request.getLastName(), request.getEmail()
        );
        return mapToUserProfileResponse(updated);
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public void updateProfileImage(MultipartFile file) {
        validateImage(file);

        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse currentUser = cachedIdentityClient.getUserById(userId);
        String oldImageUrl = currentUser.getProfileImageUrl();

        String newImageUrl = null;
        try {
            newImageUrl = fileStorageService.saveFile(file, "/profiles");

            try {
                cachedIdentityClient.updateProfileImage(userId, newImageUrl);
            } catch (Exception e) {
                // avoid orphan
                try {
                    fileStorageService.deleteFile(newImageUrl);
                } catch (Exception deleteEx) {
                    logger.warn("Failed to delete orphan file after identity update failure: {}", newImageUrl, deleteEx);
                }
                throw e;
            }

            // best-effort
            if (oldImageUrl != null && !oldImageUrl.isBlank()) {
                try {
                    fileStorageService.deleteFile(oldImageUrl);
                } catch (Exception e) {
                    logger.warn("Failed to delete old profile image: {}", oldImageUrl, e);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to update profile image for user {}", userId, e);
            throw e;
        }
    }

    @Override
    public ActiveSubscriptionResponse getActiveSubscription() {
        return ActiveSubscriptionResponse.builder().status("none").build();
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public void deleteAccount(DeleteAccountRequest request) {
        if (!Boolean.TRUE.equals(request.getConfirm())) {
            throw new BadRequestException("Confirmation must be true");
        }
        Long userId = UserContext.getCurrentUserId();
        cachedIdentityClient.deleteUser(userId, request.getReason());
    }

    @CacheEvict(cacheNames = {"user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public void updateGoal(UpdateGoalsRequest request) {
        Long userId = UserContext.getCurrentUserId();

        goalReferenceRepository.findById(request.getGoalCode())
                .orElseThrow(() -> new ResourceNotFoundException("Goal reference not found"));

        UserProfile profile = getOrCreateProfile(userId);

        if (!Objects.equals(profile.getGoalCode(), request.getGoalCode())) {
            profile.setGoalCode(request.getGoalCode());
            userProfileRepository.save(profile);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public BodyInfoResponse getBodyInfo(String language) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });

        String translatedGender = null;
        if (profile.getGender() != null) {
            translatedGender = translationService.getTranslatedValue("Gender", profile.getGender().name(), "label", language);
        }

        return BodyInfoResponse.builder()
                .heightCm(profile.getHeightCm() != null ? profile.getHeightCm().intValue() : null)
                .weightKg(profile.getWeightKg())
                .gender(translatedGender)
                .birthDate(profile.getBirthDate())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public GoalResponse getGoal(String language) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });

        String goalCode = profile.getGoalCode();
        if (goalCode == null || goalCode.isBlank()) {
            throw new ResourceNotFoundException("Goal not set for user");
        }

        var reference = goalReferenceRepository.findById(goalCode)
                .orElseThrow(() -> new ResourceNotFoundException("Goal reference not found"));

        return mapToGoalResponse(reference, goalCode, language);
    }

    @Transactional(readOnly = true)
    @Override
    public GoalsResponse getReferenceGoals() {
        var items = goalReferenceRepository.findAllByOrderByGoalCodeAsc().stream()
                .map(goal -> {
                    String title = translationService.getTranslatedValue("GoalReference", goal.getGoalCode(), "title", "AZ");
                    String subtitle = translationService.getTranslatedValue("GoalReference", goal.getGoalCode(), "subtitle", "AZ");
                    return GoalItemResponse.builder()
                            .code(goal.getGoalCode())
                            .title(title)
                            .subtitle(subtitle)
                            .imageUrl(goal.getImageUrl())
                            .build();
                })
                .toList();

        return GoalsResponse.builder().items(items).build();
    }

    @Override
    public void updatePreferences(UpdatePreferencesRequest request) {
        // placeholder
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public void updateLanguage(UpdateLanguageRequest request) {
        languageRepository.findByCode(request.getLanguage())
                .orElseThrow(() -> new BadRequestException("Invalid language code: " + request.getLanguage()));

        Long userId = UserContext.getCurrentUserId();
        cachedIdentityClient.updateLanguage(userId, request.getLanguage());
    }

    @Transactional(readOnly = true)
    @Override
    public SetupResponse getSetupStatus() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);

        return SetupResponse.builder()
                .setupRequired(identityUser.getSetupRequired())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public FitnessLevelResponse getFitnessLevel(String language) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getHeightCm() == null || profile.getWeightKg() == null) {
            throw new ConflictException("Profile incomplete");
        }

        double heightM = profile.getHeightCm() / 100.0;
        double bmi = profile.getWeightKg() / (heightM * heightM);
        bmi = Math.round(bmi * 10.0) / 10.0;

        BmiScaleResponse bmiScale = BmiScaleResponse.builder()
                .underweightMax(18.5)
                .normalMax(25.0)
                .overweightMax(30.0)
                .build();

        return FitnessLevelResponse.builder()
                .bmi(bmi)
                .bmiScale(bmiScale)
                .goal(profile.getGoalCode())
                .build();
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public CompleteSetupResponse completeSetup() {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        if (profile.getHeightCm() == null ||
                profile.getWeightKg() == null ||
                profile.getGoalCode() == null ||
                profile.getGender() == null ||
                profile.getBirthDate() == null) {
            throw new ConflictException("Setup incomplete");
        }

        cachedIdentityClient.updateSetupRequired(userId, false);

        return CompleteSetupResponse.builder()
                .setupRequired(false)
                .next(CompleteSetupResponse.NextSteps.builder()
                        .workoutPlanReady(true)
                        .nutritionPlanReady(true)
                        .build())
                .build();
    }

    @CacheEvict(cacheNames = {"user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public SetupResponse setupProfile(SetupRequest request) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = getOrCreateProfile(userId);

        if (request.getProfile() != null) {
            SetupRequest.ProfileInfo info = request.getProfile();

            if (info.getHeightCm() != null) profile.setHeightCm(info.getHeightCm().doubleValue());
            if (info.getWeightKg() != null) profile.setWeightKg(info.getWeightKg());

            if (info.getGender() != null) {
                try {
                    profile.setGender(Gender.valueOf(info.getGender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid gender value");
                }
            }

            if (info.getBirthDate() != null) profile.setBirthDate(info.getBirthDate());

            if (info.getGoal() != null) {
                goalReferenceRepository.findById(info.getGoal())
                        .orElseThrow(() -> new ResourceNotFoundException("Goal reference not found"));
                profile.setGoalCode(info.getGoal());
            }
        }

        userProfileRepository.save(profile);

        return SetupResponse.builder()
                .setupRequired(false)
                .build();
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public CompleteSetupResponse skipSetup() {
        Long userId = UserContext.getCurrentUserId();

        cachedIdentityClient.updateSetupRequired(userId, false);

        return CompleteSetupResponse.builder()
                .setupRequired(false)
                .next(CompleteSetupResponse.NextSteps.builder()
                        .workoutPlanReady(false)
                        .nutritionPlanReady(false)
                        .build())
                .build();
    }

    // ----------------- Helpers -----------------

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }
    }

    private LocalDateTime parseCreatedAt(String value) {
        if (value == null || value.isBlank()) return null;

        // 1) epoch millis
        try {
            if (value.chars().allMatch(Character::isDigit)) {
                long epochMillis = Long.parseLong(value);
                return Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
        } catch (Exception ignored) { }

        // 2) ISO local datetime
        try {
            return LocalDateTime.parse(value);
        } catch (DateTimeParseException ignored) { }

        // 3) RFC3339/ISO with offset -> preserve instant meaning
        try {
            return OffsetDateTime.parse(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException ignored) { }

        // 4) ISO instant (e.g., 2024-01-01T00:00:00Z)
        try {
            return Instant.parse(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (DateTimeParseException e) {
            logger.warn("Failed to parse createdAt: {}", value, e);
            return null;
        }
    }




    private UserProfile getOrCreateProfile(Long userId) {
        Optional<UserProfile> existing = userProfileRepository.findById(userId);
        if (existing.isPresent()) return existing.get();

        UserProfile newProfile = new UserProfile();
        newProfile.setUserId(userId);

        try {
            return userProfileRepository.save(newProfile);
        } catch (DataIntegrityViolationException e) {
            // race: someone inserted
            return userProfileRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Profile not found after create race"));
        }
    }

    private UserProfileResponse mapToUserProfileResponse(IdentityUserResponse userResponse) {
        return UserProfileResponse.builder()
                .userId(userResponse.getUserId())
                .firstName(userResponse.getFirstName())
                .lastName(userResponse.getLastName())
                .mobile(userResponse.getMobile())
                .email(userResponse.getEmail())
                .profileImageUrl(userResponse.getProfileImageUrl())
                .createdAt(parseCreatedAt(userResponse.getCreatedAt()))
                .build();
    }

    // Note: GoalReference type assumed from your repo. Ensure correct import in your project.
    private GoalItemResponse mapToGoalItemResponse(GoalReference goal) {
        return GoalItemResponse.builder()
                .code(goal.getGoalCode())
                .title("") // Placeholder, as titles are in translations
                .subtitle("") // Placeholder, as subtitles are in translations
                .imageUrl(goal.getImageUrl())
                .build();
    }

    private GoalResponse mapToGoalResponse(GoalReference reference, String goalCode, String language) {
        String title = translationService.getTranslatedValue("GoalReference", goalCode, "title", language);
        String subtitle = translationService.getTranslatedValue("GoalReference", goalCode, "subtitle", language);

        return GoalResponse.builder()
                .goalCode(goalCode)
                .title(title)
                .subtitle(subtitle)
                .imageUrl(reference.getImageUrl())
                .build();
    }
}
