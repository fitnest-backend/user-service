package az.fitnest.userservice.controller;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.service.GoalsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GoalsController {

	private final GoalsService goalsService;

	@PutMapping(value = "/me/goals", consumes = "application/json")
	public ResponseEntity<Void> updateMeGoals(
			@Valid @RequestBody UpdateGoalsRequest request
	) {
		goalsService.updateMeGoals(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@PutMapping(value = "/me/body", consumes = "application/json")
	public ResponseEntity<Void> updateMeBody(
			@Valid @RequestBody UpdateBodyRequest request
	) {
		goalsService.updateMeBody(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}