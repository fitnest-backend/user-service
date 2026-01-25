package az.fitnest.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.request.UpdateUserProfileRequest;
import az.fitnest.userservice.response.SummaryResponse;
import az.fitnest.userservice.response.UserProfileResponse;
import az.fitnest.userservice.service.UserProfileService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

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
	public ResponseEntity<Void> updateUserMe(@RequestBody UpdateUserProfileRequest request) {
		  userProfileService.updateUserMe(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}


}
