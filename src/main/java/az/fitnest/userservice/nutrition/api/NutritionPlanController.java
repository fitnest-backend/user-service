package az.fitnest.userservice.nutrition.api;

import az.fitnest.userservice.nutrition.api.dto.response.NutritionPlanResponse;
import az.fitnest.userservice.nutrition.api.dto.response.NutritionPlanResponses;
import az.fitnest.userservice.nutrition.adapter.service.NutritionPlansService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class NutritionPlanController {

	private final NutritionPlansService nutritionPlansService;

	@GetMapping("/nutrition-plans")
	public ResponseEntity<NutritionPlanResponses> getNutritionPlans() {
		NutritionPlanResponses nutritionPlans = nutritionPlansService.getNutritionPlans();
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlans);
	}

	@GetMapping("/nutrition-plans/{planId}")
	public ResponseEntity<NutritionPlanResponse> getNutritionPlan(@PathVariable Long planId) {
		NutritionPlanResponse nutritionPlan = nutritionPlansService.getNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlan);
	}
	
	@GetMapping("/nutrition-plans/{planId}/active")
	public ResponseEntity<Void> getActiveNutritionPlan(@PathVariable Long planId) {
		 nutritionPlansService.getActiveNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}
