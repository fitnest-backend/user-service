package az.fitnest.user.profile.api;

import az.fitnest.user.profile.adapter.service.UserProfileService;
import az.fitnest.user.profile.api.dto.request.*;
import az.fitnest.user.profile.api.dto.response.*;
import az.fitnest.user.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserSummary()));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserMe()));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMe(@Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateUserMe(request)));
    }

    @PutMapping("/location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(@Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateMyLocation(request)));
    }

    @PutMapping("/body")
    public ResponseEntity<ApiResponse<Void>> updateBody(@Valid @RequestBody UpdateBodyRequest request) {
        userProfileService.updateBody(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/profile-image")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfileImage(@RequestParam("image") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateProfileImage(file)));
    }

    @GetMapping("/reference/goals")
    public ResponseEntity<ApiResponse<GoalsResponse>> getReferenceGoals() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getReferenceGoals()));
    }

    @PutMapping("/goal")
    public ResponseEntity<ApiResponse<Void>> updateGoal(@Valid @RequestBody UpdateGoalsRequest request) {
        userProfileService.updateGoal(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(@Valid @RequestBody UpdatePreferences request) {
        userProfileService.updatePreferences(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> getSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getSetupStatus()));
    }

    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> setupProfile(@Valid @RequestBody SetupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.setupProfile(request)));
    }

    @PostMapping("/complete-setup")
    public ResponseEntity<ApiResponse<CompleteSetupResponse>> completeSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.completeSetup()));
    }

    @GetMapping("/fitness-level")
    public ResponseEntity<ApiResponse<FitnessLevelResponse>> getFitnessLevel() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getFitnessLevel()));
    }

    @GetMapping("/active-subscription")
    public ResponseEntity<ApiResponse<ActiveSubscriptionResponse>> getActiveSubscription() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getActiveSubscription()));
    }

    @DeleteMapping("/account")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        userProfileService.deleteAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
