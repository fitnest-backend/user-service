package az.fitnest.user.user.api;

import az.fitnest.user.user.api.dto.request.DeleteAccountRequest;
import az.fitnest.user.user.api.dto.request.SetupRequest;
import az.fitnest.user.user.api.dto.request.UpdateLocationRequest;
import az.fitnest.user.user.api.dto.request.UpdateProfileImageRequest;
import az.fitnest.user.user.api.dto.request.UpdateUserProfileRequest;
import az.fitnest.user.user.api.dto.response.ActiveSubscriptionResponse;
import az.fitnest.user.user.api.dto.response.LocationResponse;
import az.fitnest.user.user.api.dto.response.SetupResponse;
import az.fitnest.user.user.api.dto.response.SummaryResponse;
import az.fitnest.user.user.api.dto.response.UserProfileResponse;
import az.fitnest.user.user.adapter.service.UserProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
@Tag(name = "Me", description = "Endpoints for current user profile and settings")
public class UserProfileController {

	private final UserProfileService userProfileService;

	@Operation(summary = "Get home summary", description = "Returns user header info and counters for Home screen.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Summary returned", content = @Content(schema = @Schema(implementation = SummaryResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/me/summary")
	public ResponseEntity<SummaryResponse> getUserSummary() {
		SummaryResponse userSummary = userProfileService.getUserSummary();
		return ResponseEntity.status(HttpStatus.OK).body(userSummary);
	}

	@Operation(summary = "Update my location", description = "Stores user coordinates for nearby features.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Location updated", content = @Content(schema = @Schema(implementation = LocationResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping("/me/location")
	public ResponseEntity<LocationResponse> updateMyLocation(@Valid @RequestBody UpdateLocationRequest request) {
		LocationResponse response = userProfileService.updateMyLocation(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@Operation(summary = "Get my profile", description = "Returns current user profile information.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile returned", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getUserMe() {
		UserProfileResponse userMe = userProfileService.getUserMe();
		return ResponseEntity.status(HttpStatus.OK).body(userMe);
	}
	
	@Operation(summary = "Update my profile", description = "Updates editable user profile fields.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile updated", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponse> updateUserMe(
			@Valid @RequestBody UpdateUserProfileRequest request) {
		UserProfileResponse updated = userProfileService.updateUserMe(request);
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@Operation(summary = "Update profile photo (file)", description = "Uploads a profile image file and updates profile image URL.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile image updated", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PostMapping(value = "/me/profile-photo", consumes = "multipart/form-data")
	public ResponseEntity<UserProfileResponse> updateProfilePhotoWithFile(
			@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
		UserProfileResponse updated = userProfileService.updateProfileImage(file);
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@Operation(summary = "Update profile photo (URL)", description = "Updates profile image URL directly.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile image updated", content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PostMapping(value = "/me/profile-photo", consumes = "application/json")
	public ResponseEntity<UserProfileResponse> updateProfilePhotoWithUrl(
			@Valid @RequestBody UpdateProfileImageRequest request) {
		UserProfileResponse updated = userProfileService.updateProfileImage(request.getImageUrl());
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@Operation(summary = "Get active subscription", description = "Returns active subscription status for current user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Subscription returned", content = @Content(schema = @Schema(implementation = ActiveSubscriptionResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/me/subscriptions/active")
	public ResponseEntity<ActiveSubscriptionResponse> getActiveSubscription() {
		ActiveSubscriptionResponse subscription = userProfileService.getActiveSubscription();
		return ResponseEntity.status(HttpStatus.OK).body(subscription);
	}

	@Operation(summary = "Get setup status", description = "Returns setup flow status and previously entered values.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Setup status returned", content = @Content(schema = @Schema(implementation = SetupResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/me/setup")
	public ResponseEntity<SetupResponse> getSetupStatus() {
		SetupResponse response = userProfileService.getSetupStatus();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Setup profile", description = "Saves onboarding/setup profile data.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Setup saved", content = @Content(schema = @Schema(implementation = SetupResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping("/me/setup")
	public ResponseEntity<SetupResponse> setupProfile(@Valid @RequestBody SetupRequest request) {
		SetupResponse response = userProfileService.setupProfile(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Update user profile", description = "Saves user body information: height, weight, gender, age")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Profile updated", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping("/me/profile")
	public ResponseEntity<Void> updateProfile(@Valid @RequestBody az.fitnest.user.user.api.dto.request.UpdateBodyRequest request) {
		userProfileService.updateBody(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Get fitness level", description = "Returns BMI and fitness level calculations.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Fitness level calculated", content = @Content(schema = @Schema(implementation = az.fitnest.user.user.api.dto.response.FitnessLevelResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "409", description = "Profile incomplete", content = @Content)
	})
	@GetMapping("/me/fitness-level")
	public ResponseEntity<az.fitnest.user.user.api.dto.response.FitnessLevelResponse> getFitnessLevel() {
		az.fitnest.user.user.api.dto.response.FitnessLevelResponse response = userProfileService.getFitnessLevel();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Complete setup", description = "Completes onboarding/setup process.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Setup completed", content = @Content(schema = @Schema(implementation = az.fitnest.user.user.api.dto.response.CompleteSetupResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "409", description = "Setup incomplete", content = @Content)
	})
	@PostMapping("/me/setup/complete")
	public ResponseEntity<az.fitnest.user.user.api.dto.response.CompleteSetupResponse> completeSetup() {
		az.fitnest.user.user.api.dto.response.CompleteSetupResponse response = userProfileService.completeSetup();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Delete my account", description = "Deletes current user account.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Account deleted", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "409", description = "Conflict", content = @Content)
	})
	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
		userProfileService.deleteAccount(request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
