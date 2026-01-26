package az.fitnest.user.goal.api;

import az.fitnest.user.user.api.dto.request.UpdateBodyRequest;
import az.fitnest.user.goal.api.dto.request.UpdateGoalsRequest;
import az.fitnest.user.goal.api.dto.response.GoalsResponse;
import az.fitnest.user.goal.adapter.service.GoalReferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class GoalsController {

	private final GoalReferenceService goalsService;

	@GetMapping("/reference/goals")
	public ResponseEntity<GoalsResponse> getGoals() {
		GoalsResponse response = goalsService.getGoals();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

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