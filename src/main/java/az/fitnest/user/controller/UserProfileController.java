package az.fitnest.user.controller;

import az.fitnest.user.service.UserProfileService;
import az.fitnest.user.dto.*;
import az.fitnest.user.dto.request.*;
import az.fitnest.user.dto.response.*;
import az.fitnest.user.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.util.UserContext;
import az.fitnest.user.repository.TranslationRepository;
import az.fitnest.user.model.entity.Translation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/v1/me")
@RequiredArgsConstructor
@Tag(name = "User Profile", description = "Endpoints for managing user profile, settings, and account")
public class UserProfileController {

    private static final Logger logger = LoggerFactory.getLogger(UserProfileController.class);

    private final UserProfileService userProfileService;
    private final CachedIdentityGrpcClient cachedIdentityGrpcClient;
    private final TranslationRepository translationRepository;

    @Operation(summary = "Get user summary", description = "Returns a brief summary of the user's profile and progress.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Summary retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SummaryResponse.class), examples = @ExampleObject(value = "{\"totalWorkouts\": 25, \"totalCalories\": 1500}")))
    })
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<SummaryResponse>> getSummary() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserSummary()));
    }

    @Operation(summary = "Get current user profile", description = "Returns the full profile details of the authenticated user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class), examples = @ExampleObject(value = "{\"id\": 1, \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}")))
    })
    @GetMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> getMe() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getUserMe()));
    }

    @Operation(summary = "Update user profile", description = "Updates the authenticated user's profile information such as name, email, and other personal details. Only provided fields will be updated, leaving others unchanged. Validation is performed on the input data.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class), examples = @ExampleObject(value = "{\"id\": 1, \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"john.doe@example.com\"}")))
    })
    @PutMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateMe(@Valid @RequestBody UpdateUserProfileRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateUserMe(request)));
    }

    @Operation(summary = "Update user location", description = "Updates the user's current city and country.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Location updated successfully",
                    content = @Content(schema = @Schema(implementation = LocationResponse.class)))
    })
    @PutMapping("/location")
    public ResponseEntity<ApiResponse<LocationResponse>> updateLocation(@Valid @RequestBody UpdateLocationRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.updateMyLocation(request)));
    }

    @Operation(summary = "Update body metrics", description = "Updates user's physical metrics like height, weight, etc.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Body metrics updated successfully", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/body")
    public ResponseEntity<ApiResponse<Void>> updateBody(@Valid @RequestBody UpdateBodyRequest request) {
        userProfileService.updateBody(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get body metrics", description = "Returns user's physical metrics.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Body metrics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = BodyInfoResponse.class)))
    })
    @GetMapping("/body")
    public ResponseEntity<ApiResponse<BodyInfoResponse>> getBody() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getBodyInfo(userLanguage)));
    }

    @Operation(summary = "Update profile image", description = "Uploads and sets a new profile image for the user.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
            schema = @Schema(implementation = ProfileImageUploadRequest.class)))
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile image updated successfully")
    })
    @PutMapping(value = "/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateProfileImage(@RequestPart("image") MultipartFile file) {
        userProfileService.updateProfileImage(file);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get goal references", description = "Returns available health and fitness goals for reference.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal references retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GoalsResponse.class)))
    })
    @GetMapping("/reference/goals")
    public ResponseEntity<ApiResponse<GoalsResponse>> getReferenceGoals() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getReferenceGoals()));
    }

    @Operation(summary = "Update user goal", description = "Updates the primary fitness or health goal of the user.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal updated successfully", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/goal")
    public ResponseEntity<ApiResponse<Void>> updateGoal(@Valid @RequestBody UpdateGoalsRequest request) {
        userProfileService.updateGoal(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get user goal", description = "Returns the user's current goal.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal retrieved successfully",
                    content = @Content(schema = @Schema(implementation = GoalResponse.class)))
    })
    @GetMapping("/goal")
    public ResponseEntity<ApiResponse<GoalResponse>> getGoal() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getGoal(userLanguage)));
    }

    @Operation(summary = "Update user preferences", description = "Updates application settings like language and theme.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Preferences updated successfully", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(@Valid @RequestBody UpdatePreferencesRequest request) {
        userProfileService.updatePreferences(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Update user language", description = "Updates the user's preferred language.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language updated successfully", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @PutMapping("/language")
    public ResponseEntity<ApiResponse<Void>> updateLanguage(@Valid @RequestBody UpdateLanguageRequest request) {
        userProfileService.updateLanguage(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get setup status", description = "Returns the current progress of the user profile setup.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setup status retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SetupResponse.class)))
    })
    @GetMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> getSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getSetupStatus()));
    }

    @Operation(summary = "Initial profile setup", description = "Sets up the user's profile with required initial details.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Profile setup step completed successfully",
                    content = @Content(schema = @Schema(implementation = SetupResponse.class)))
    })
    @PostMapping("/setup")
    public ResponseEntity<ApiResponse<SetupResponse>> setupProfile(@Valid @RequestBody SetupRequest request) {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.setupProfile(request)));
    }

    @Operation(summary = "Complete profile setup", description = "Finalizes the user profile setup process.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setup completed successfully",
                    content = @Content(schema = @Schema(implementation = CompleteSetupResponse.class)))
    })
    @PostMapping("/setup/complete")
    public ResponseEntity<ApiResponse<CompleteSetupResponse>> completeSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.completeSetup()));
    }

    @Operation(summary = "Skip profile setup", description = "Allows the user to skip the profile setup process.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Setup skipped successfully",
                    content = @Content(schema = @Schema(implementation = CompleteSetupResponse.class)))
    })
    @PostMapping("/setup/skip")
    public ResponseEntity<ApiResponse<CompleteSetupResponse>> skipSetup() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.skipSetup()));
    }

    @Operation(summary = "Get fitness level", description = "Returns the user's current fitness level.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Fitness level retrieved successfully",
                    content = @Content(schema = @Schema(implementation = FitnessLevelResponse.class)))
    })
    @GetMapping("/fitness-level")
    public ResponseEntity<ApiResponse<FitnessLevelResponse>> getFitnessLevel() {
        String userLanguage = getUserLanguage();
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getFitnessLevel(userLanguage)));
    }

    @Operation(summary = "Get active subscription", description = "Returns the user's active subscription details.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Subscription details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ActiveSubscriptionResponse.class)))
    })
    @GetMapping("/subscription")
    public ResponseEntity<ApiResponse<ActiveSubscriptionResponse>> getActiveSubscription() {
        return ResponseEntity.ok(ApiResponse.success(userProfileService.getActiveSubscription()));
    }

    @Operation(summary = "Delete account", description = "Deletes the user's account and associated data.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Account deleted successfully", content = @Content(examples = @ExampleObject(value = "null")))
    })
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
        userProfileService.deleteAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    private String getUserLanguage() {
        Long userId = UserContext.getCurrentUserId();
        if (userId != null) {
            try {
                az.fitnest.user.grpc.UserResponse user = cachedIdentityGrpcClient.getUserById(userId);
                String language = user.getLanguage();
                if (language != null && !language.isEmpty()) {
                    return language.toUpperCase();
                }
            } catch (Exception e) {
                // Log error or ignore
            }
        }
        return "AZ";
    }
}
