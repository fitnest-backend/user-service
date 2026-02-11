package az.fitnest.user.service;

import az.fitnest.user.dto.*;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    SummaryResponse getUserSummary();
    UserProfileResponse getUserMe();
    LocationResponse updateMyLocation(UpdateLocationRequest request);
    void updateBody(UpdateBodyRequest request);
    UserProfileResponse updateUserMe(UpdateUserProfileRequest request);
    UserProfileResponse updateProfileImage(MultipartFile file);
    ActiveSubscriptionResponse getActiveSubscription();
    void deleteAccount(DeleteAccountRequest request);
    void updateGoal(UpdateGoalsRequest request);
    BodyInfoResponse getBodyInfo();
    GoalResponse getGoal();
    void updatePreferences(UpdatePreferencesRequest request);
    GoalsResponse getReferenceGoals();
    SetupResponse getSetupStatus();
    FitnessLevelResponse getFitnessLevel();
    CompleteSetupResponse completeSetup();
    SetupResponse setupProfile(SetupRequest request);
}
