package az.fitnest.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.request.UpdateBodyRequest;
import az.fitnest.userservice.request.UpdateGoalsRequest;
import az.fitnest.userservice.service_inter.GoalsInter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class GoalsController {

	private final GoalsInter goalsInter;

	@PutMapping("/me/goals")
	public ResponseEntity<Void> updateMeGoals(@RequestBody UpdateGoalsRequest request) {
		goalsInter.updateMeGoals(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	@PutMapping("/me/body")
	public ResponseEntity<Void> updateMeBody(@RequestBody UpdateBodyRequest request) {
		goalsInter.updateMeBody(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
