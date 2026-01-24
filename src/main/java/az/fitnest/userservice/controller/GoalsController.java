package az.fitnest.userservice.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.service.GoalsService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class GoalsController {

	private final GoalsService goalsService;

	@PutMapping(path = "/me/goals", consumes = "application/json")
	public ResponseEntity<Void> updateMeGoals(@RequestBody  UpdateGoalsRequest request) {
		goalsService.updateMeGoals(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	@PutMapping("/me/body")
	public ResponseEntity<Void> updateMeBody(@RequestBody UpdateBodyRequest request) {
		goalsService.updateMeBody(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
