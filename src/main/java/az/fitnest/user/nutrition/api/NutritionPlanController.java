package az.fitnest.user.nutrition.api;

import az.fitnest.user.nutrition.api.dto.response.NutritionPlanResponse;
import az.fitnest.user.nutrition.api.dto.response.NutritionPlanResponses;
import az.fitnest.user.nutrition.adapter.service.NutritionPlansService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Nutrition", description = "Endpoints for nutrition plans")
public class NutritionPlanController {

	private final NutritionPlansService nutritionPlansService;

	@Operation(summary = "List nutrition plans", description = "Returns nutrition plans list.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Nutrition plans returned", content = @Content(schema = @Schema(implementation = NutritionPlanResponses.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/nutrition-plans")
	public ResponseEntity<NutritionPlanResponses> getNutritionPlans() {
		NutritionPlanResponses nutritionPlans = nutritionPlansService.getNutritionPlans();
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlans);
	}

	@Operation(summary = "Get nutrition plan", description = "Returns nutrition plan by id.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Nutrition plan returned", content = @Content(schema = @Schema(implementation = NutritionPlanResponse.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content)
	})
	@GetMapping("/nutrition-plans/{planId}")
	public ResponseEntity<NutritionPlanResponse> getNutritionPlan(@PathVariable Long planId) {
		NutritionPlanResponse nutritionPlan = nutritionPlansService.getNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(nutritionPlan);
	}
	
	@Operation(summary = "Activate nutrition plan", description = "Marks a nutrition plan as active for current user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Nutrition plan activated", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Not found", content = @Content)
	})
	@GetMapping("/nutrition-plans/{planId}/active")
	public ResponseEntity<Void> getActiveNutritionPlan(@PathVariable Long planId) {
		 nutritionPlansService.getActiveNutritionPlan(planId);
		return ResponseEntity.status(HttpStatus.OK).body(null);
	}

}
