package az.fitnest.user.service.impl;

import az.fitnest.user.client.CachedIdentityGrpcClient;

import az.fitnest.user.dto.request.*;
import az.fitnest.user.dto.response.*;
import az.fitnest.user.mapper.UserProfileMapper;
import az.fitnest.user.client.OrderGrpcClient;
import az.fitnest.user.exception.BadRequestException;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.model.entity.UserLocation;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.model.enums.Gender;
import az.fitnest.user.repository.LanguageRepository;
import az.fitnest.user.repository.UserLocationRepository;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.service.*;
import az.fitnest.user.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import az.fitnest.catalog.grpc.GymMainPage;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {


    private final CachedIdentityGrpcClient cachedIdentityClient;
    private final UserProfileRepository userProfileRepository;
    private final FileStorageService fileStorageService;
    private final UserLocationRepository userLocationRepository;
    private final az.fitnest.user.repository.GoalReferenceRepository goalReferenceRepository;
    private final LanguageRepository languageRepository;
    private final TranslationService translationService;
    private final az.fitnest.user.client.StorageGrpcClient storageGrpcClient;
    private final CatalogGrpcClient catalogGrpcClient;
    private final OrderGrpcClient orderGrpcClient;
    private final LanguageService languageService;
    private final org.springframework.context.MessageSource messageSource;

    private Long currentUserId() {
        return UserContext.getCurrentUserId();
    }

    @Cacheable(cacheNames = "user_summaries", key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", sync = true)
    @Transactional(readOnly = true)
    @Override
    public SummaryResponse getUserSummary() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);

        String profileImageUrl = identityUser.profileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            profileImageUrl = "/api/v1/me/profile/images/" + profileImageUrl;
        } else {
            profileImageUrl = null;
        }

        String currentSubscription = null;
        String subscriptionStatus = null;
        try {
            az.fitnest.order.grpc.ActiveSubscriptionResponse r = orderGrpcClient.getActiveSubscription(userId);

            // Get package name (could be "No Plan")
            if (r.getPackageName() != null && !r.getPackageName().isEmpty()) {
                currentSubscription = r.getPackageName();
            }

            // Only set status for active or frozen subscriptions, not for "none"/"No Plan"
            if (r.getSubscriptionStatus() != null && !r.getSubscriptionStatus().isEmpty()
                    && !r.getSubscriptionStatus().equals("none")) {
                subscriptionStatus = r.getSubscriptionStatus();
            }
        } catch (Exception e) {
            // If order-service is unavailable, set default "No Plan"
            currentSubscription = "No Plan";
        }

        UserProfileResponse user = UserProfileMapper.toUserProfileResponse(identityUser, profileImageUrl, currentSubscription, subscriptionStatus);

        CountersResponse counters = CountersResponse.builder()
                .favorite_gyms(0L)
                .favorite_stores(0L)
                .build();

        return SummaryResponse.builder()
                .user(user)
                .counters(counters)
                .unreadNotifications(0)
                .build();
    }

    @Cacheable(cacheNames = "user_me", key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()")
    @Override
    public UserProfileResponse getUserMe() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);
        String profileImageUrl = identityUser.profileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            profileImageUrl = "/api/v1/me/profile/images/" + profileImageUrl;
        } else {
            profileImageUrl = null;
        }

        String currentSubscription = null;
        String subscriptionStatus = null;
        try {
            az.fitnest.order.grpc.ActiveSubscriptionResponse r = orderGrpcClient.getActiveSubscription(userId);

            // Get package name (could be "No Plan")
            if (r.getPackageName() != null && !r.getPackageName().isEmpty()) {
                currentSubscription = r.getPackageName();
            }

            // Only set status for active or frozen subscriptions, not for "none"/"No Plan"
            if (r.getSubscriptionStatus() != null && !r.getSubscriptionStatus().isEmpty()
                    && !r.getSubscriptionStatus().equals("none")) {
                subscriptionStatus = r.getSubscriptionStatus();
            }
        } catch (Exception e) {
            // If order-service is unavailable, set default "No Plan"
            currentSubscription = "No Plan";
        }

        return UserProfileMapper.toUserProfileResponse(identityUser, profileImageUrl, currentSubscription, subscriptionStatus);
    }

    private UserProfile getOrCreateProfile(Long userId) {
        return userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile newProfile = new UserProfile();
            newProfile.setUserId(userId);
            return userProfileRepository.save(newProfile);
        });
    }

    @Transactional
    @Override
    public LocationResponse updateMyLocation(UpdateLocationRequest request) {
        Long userId = UserContext.getCurrentUserId();

        var existingOpt = userLocationRepository.findById(userId);
        UserLocation location = existingOpt.orElseGet(() ->
                new UserLocation(userId, request.lat(), request.lng(), LocalDateTime.now())
        );

        boolean isNew = existingOpt.isEmpty();
        boolean latChanged = !Objects.equals(location.getLat(), request.lat());
        boolean lngChanged = !Objects.equals(location.getLng(), request.lng());

        if (isNew || latChanged || lngChanged) {
            location.setLat(request.lat());
            location.setLng(request.lng());
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
        checkSetupNotRequired();
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = getOrCreateProfile(userId);

        boolean dirty = false;

        if (request.heightCm() != null) {
            Double newHeight = request.heightCm().doubleValue();
            if (!Objects.equals(profile.getHeightCm(), newHeight)) {
                profile.setHeightCm(newHeight);
                dirty = true;
            }
        }
        if (request.weightKg() != null) {
            if (!Objects.equals(profile.getWeightKg(), request.weightKg())) {
                profile.setWeightKg(request.weightKg());
                dirty = true;
            }
        }
        if (request.gender() != null) {
            if (!Objects.equals(profile.getGender(), request.gender())) {
                profile.setGender(request.gender());
                dirty = true;
            }
        }
        if (request.birthDate() != null) {
            if (!Objects.equals(profile.getBirthDate(), request.birthDate())) {
                profile.setBirthDate(request.birthDate());
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
                userId, request.firstName(), request.lastName()
        );
        String profileImageUrl = updated.profileImageUrl();
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            profileImageUrl = "/api/v1/me/profile/images/" + profileImageUrl;
        } else {
            profileImageUrl = null;
        }

        String currentSubscription = null;
        String subscriptionStatus = null;
        try {
            az.fitnest.order.grpc.ActiveSubscriptionResponse r = orderGrpcClient.getActiveSubscription(userId);

            // Get package name (could be "No Plan")
            if (r.getPackageName() != null && !r.getPackageName().isEmpty()) {
                currentSubscription = r.getPackageName();
            }

            // Only set status for active or frozen subscriptions, not for "none"/"No Plan"
            if (r.getSubscriptionStatus() != null && !r.getSubscriptionStatus().isEmpty()
                    && !r.getSubscriptionStatus().equals("none")) {
                subscriptionStatus = r.getSubscriptionStatus();
            }
        } catch (Exception e) {
            // If order-service is unavailable, set default "No Plan"
            currentSubscription = "No Plan";
        }

        return UserProfileMapper.toUserProfileResponse(updated, profileImageUrl, currentSubscription, subscriptionStatus);
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public void updateProfileImage(MultipartFile file) {
        validateImage(file);

        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse currentUser = cachedIdentityClient.getUserById(userId);
        String oldImageUrl = currentUser.profileImageUrl();

        String newImageUrl = null;
        try {
            // Pass oldImageUrl to storage service for atomic replacement
            newImageUrl = fileStorageService.saveFile(file, "/profiles", oldImageUrl);

            try {
                cachedIdentityClient.updateProfileImage(userId, newImageUrl);
            } catch (Exception e) {
                // avoid orphan
                try {
                    fileStorageService.deleteFile(newImageUrl);
                } catch (Exception deleteEx) {
                }
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }
    }



    @CacheEvict(cacheNames = {"user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public void updateGoal(UpdateGoalsRequest request) {
        checkSetupNotRequired();
        Long userId = UserContext.getCurrentUserId();

        goalReferenceRepository.findById(request.goalCode())
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));

        UserProfile profile = getOrCreateProfile(userId);

        if (!Objects.equals(profile.getGoalCode(), request.goalCode())) {
            profile.setGoalCode(request.goalCode());
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
            throw new ResourceNotFoundException("error.user_goal_not_set");
        }

        var reference = goalReferenceRepository.findById(goalCode)
                .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));

        String title = translationService.getTranslatedValue("GoalReference", goalCode, "title", language);
        String subtitle = translationService.getTranslatedValue("GoalReference", goalCode, "subtitle", language);
        return UserProfileMapper.toGoalResponse(reference, goalCode, title, subtitle);
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
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Override
    public void updateLanguage(UpdateLanguageRequest request) {
        languageRepository.findByCode(request.language())
                .orElseThrow(() -> new BadRequestException("error.invalid_language_code"));

        Long userId = UserContext.getCurrentUserId();
        cachedIdentityClient.updateLanguage(userId, request.language());
    }

    @Transactional(readOnly = true)
    @Override
    public SetupResponse getSetupStatus() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);

        return SetupResponse.builder()
                .setupRequired(identityUser.setupRequired())
                .build();
    }

    @Transactional(readOnly = true)
    @Override
    public FitnessLevelResponse getFitnessLevel(String language) {
        Long userId = UserContext.getCurrentUserId();
        UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
            UserProfile p = new UserProfile();
            p.setUserId(userId);
            return p;
        });

        Double bmiValue = null;
        if (profile.getHeightCm() != null && profile.getWeightKg() != null
                && profile.getGender() != null && profile.getBirthDate() != null) {
            double heightM = profile.getHeightCm() / 100.0;
            double bmi = profile.getWeightKg() / (heightM * heightM);
            bmi = Math.round(bmi * 10.0) / 10.0;
            bmiValue = bmi;
        }

        String goalTitle = null;
        if (profile.getGoalCode() != null && !profile.getGoalCode().isBlank()) {
            goalTitle = translationService.getTranslatedValue("GoalReference", profile.getGoalCode(), "title", language);
        }

        return FitnessLevelResponse.builder()
                .bmi(bmiValue)
                .goal(goalTitle)
                .build();
    }

    @CacheEvict(cacheNames = {"identity_users", "user_me", "user_summaries"}, key = "T(az.fitnest.user.util.UserContext).getCurrentUserId()", beforeInvocation = false)
    @Transactional
    @Override
    public CompleteSetupResponse completeSetup() {
        Long userId = UserContext.getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("error.profile_not_found"));

        if (profile.getHeightCm() == null ||
                profile.getWeightKg() == null ||
                profile.getGoalCode() == null ||
                profile.getGender() == null ||
                profile.getBirthDate() == null) {
            throw new ConflictException("error.setup_not_finished");
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

        if (request.profile() != null) {
            ProfileInfo info = request.profile();

            if (info.heightCm() != null) profile.setHeightCm(info.heightCm().doubleValue());
            if (info.weightKg() != null) profile.setWeightKg(info.weightKg());

            if (info.gender() != null) {
                try {
                    profile.setGender(Gender.valueOf(info.gender().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new BadRequestException("error.invalid_gender");
                }
            }

            if (info.birthDate() != null) profile.setBirthDate(info.birthDate());

            if (info.goal() != null) {
                goalReferenceRepository.findById(info.goal())
                        .orElseThrow(() -> new ResourceNotFoundException("error.goal_reference_not_found"));
                profile.setGoalCode(info.goal());
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

    @Override
    public List<LanguageDto> getAllLanguages() {
        return languageService.getAllLanguages();
    }

    @Override
    public LanguageDto getCurrentLanguage() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);
        String langCode = identityUser.language();
        if (langCode == null || langCode.isBlank()) {
            langCode = "AZ"; // Default
        }
        return languageService.getLanguageByCode(langCode);
    }

    public List<GymMainPage> getMainPageGymsFromCatalog() {
        return catalogGrpcClient.getMainPageGyms().getItemsList();
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("error.file_required");
        }
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("error.file_size_limit");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("error.only_images_allowed");
        }
    }

    private void checkSetupNotRequired() {
        Long userId = UserContext.getCurrentUserId();
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(userId);
        if (Boolean.TRUE.equals(identityUser.setupRequired())) {
            throw new BadRequestException("error.setup_not_finished");
        }
    }

}
