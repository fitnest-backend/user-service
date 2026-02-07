package az.fitnest.user.goal.api;

import az.fitnest.user.user.api.dto.request.UpdateBodyRequest;
import az.fitnest.user.goal.api.dto.request.UpdateGoalsRequest;
import az.fitnest.user.goal.api.dto.response.GoalsResponse;
import az.fitnest.user.goal.adapter.service.GoalReferenceService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Goals", description = "Endpoints for goals and body data")
public class GoalsController {

	private final GoalReferenceService goalsService;

	@Operation(summary = "Get reference goals", description = "Returns reference goals list for onboarding.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Goals returned", content = @Content(schema = @Schema(implementation = GoalsResponse.class)))
	})
	@GetMapping("/reference/goals")
	public ResponseEntity<GoalsResponse> getGoals() {
		GoalsResponse response = goalsService.getGoals();
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	@Operation(summary = "Get reference goals (alias)", description = "Returns reference goals list. Alias for /reference/goals.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Goals returned", content = @Content(schema = @Schema(implementation = GoalsResponse.class)))
	})
	@GetMapping("/me/reference/goals")
	public ResponseEntity<GoalsResponse> getMeReferenceGoals() {
		return getGoals();
	}

	@Operation(summary = "Update my goals", description = "Updates current user's selected goals.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Goals updated", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping(value = "/me/goals", consumes = "application/json")
	public ResponseEntity<Void> updateMeGoals(
			@Valid @RequestBody UpdateGoalsRequest request
	) {
		goalsService.updateMeGoals(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Update my goal (alias)", description = "Updates current user's goal. Alias for /me/goals.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Goal updated", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping(value = "/me/goal", consumes = "application/json")
	public ResponseEntity<Void> updateMeGoal(
			@Valid @RequestBody az.fitnest.user.goal.api.dto.request.UpdateGoalRequest request
	) {
		UpdateGoalsRequest serviceRequest = new UpdateGoalsRequest();
		serviceRequest.setGoalCode(request.getGoal());
		
		goalsService.updateMeGoals(serviceRequest);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

	@Operation(summary = "Update my body", description = "Updates current user's body information.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Body updated", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@PutMapping(value = "/me/body", consumes = "application/json")
	public ResponseEntity<Void> updateMeBody(
			@Valid @RequestBody UpdateBodyRequest request
	) {
		goalsService.updateMeBody(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}