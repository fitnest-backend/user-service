package az.fitnest.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.request.UpdateGoalsRequest;
import az.fitnest.userservice.response.NutritionPlanResponse;
import az.fitnest.userservice.response.NutritionPlanResponses;
import az.fitnest.userservice.service_inter.NutritionPlansInter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class NutritionPlanController {

	private final NutritionPlansInter nutritionPlansInter;

	@GetMapping("/nutrition-plans")
	public ResponseEntity<NutritionPlanResponses> getNutritionPlans() {
		NutritionPlanResponses nutritionPlans = nutritionPlansInter.getNutritionPlans();
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlans);
	}

	@GetMapping("/nutrition-plans/{planId}")
	public ResponseEntity<NutritionPlanResponse> getNutritionPlan(@PathVariable Long planId) {
		NutritionPlanResponse nutritionPlan = nutritionPlansInter.getNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlan);
	}
	
	@GetMapping("/nutrition-plans/{planId}/active")
	public ResponseEntity<Void> getActiveNutritionPlan(@PathVariable Long planId) {
		 nutritionPlansInter.getActiveNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}
