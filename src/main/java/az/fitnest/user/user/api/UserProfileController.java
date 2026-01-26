package az.fitnest.user.user.api;

import az.fitnest.user.user.api.dto.request.DeleteAccountRequest;
import az.fitnest.user.user.api.dto.request.SetupRequest;
import az.fitnest.user.user.api.dto.request.UpdateProfileImageRequest;
import az.fitnest.user.user.api.dto.request.UpdateUserProfileRequest;
import az.fitnest.user.user.api.dto.response.ActiveSubscriptionResponse;
import az.fitnest.user.user.api.dto.response.SetupResponse;
import az.fitnest.user.user.api.dto.response.SummaryResponse;
import az.fitnest.user.user.api.dto.response.UserProfileResponse;
import az.fitnest.user.user.adapter.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class UserProfileController {

	private final UserProfileService userProfileService;

	@GetMapping("/me/summary")
	public ResponseEntity<SummaryResponse> getUserSummary() {
		SummaryResponse userSummary = userProfileService.getUserSummary();
		return ResponseEntity.status(HttpStatus.OK).body(userSummary);
	}
	
	@GetMapping("/me")
	public ResponseEntity<UserProfileResponse> getUserMe() {
		UserProfileResponse userMe = userProfileService.getUserMe();
		return ResponseEntity.status(HttpStatus.OK).body(userMe);
	}
	
	@PutMapping("/me")
	public ResponseEntity<UserProfileResponse> updateUserMe(
			@Valid @RequestBody UpdateUserProfileRequest request) {
		UserProfileResponse updated = userProfileService.updateUserMe(request);
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@PostMapping("/me/profile-photo")
	public ResponseEntity<UserProfileResponse> updateProfilePhoto(
			@Valid @RequestBody UpdateProfileImageRequest request) {
		UserProfileResponse updated = userProfileService.updateProfileImage(request.getImageUrl());
		return ResponseEntity.status(HttpStatus.OK).body(updated);
	}

	@GetMapping("/me/subscriptions/active")
	public ResponseEntity<ActiveSubscriptionResponse> getActiveSubscription() {
		ActiveSubscriptionResponse subscription = userProfileService.getActiveSubscription();
		return ResponseEntity.status(HttpStatus.OK).body(subscription);
	}

	@PutMapping("/me/setup")
	public ResponseEntity<SetupResponse> setupProfile(@Valid @RequestBody SetupRequest request) {
		SetupResponse response = userProfileService.setupProfile(request);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@DeleteMapping("/me")
	public ResponseEntity<Void> deleteAccount(@Valid @RequestBody DeleteAccountRequest request) {
		userProfileService.deleteAccount(request);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
