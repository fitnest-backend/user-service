package az.fitnest.user.plan.nutrition.adapter.api;

import az.fitnest.user.plan.nutrition.adapter.service.NutritionPlansService;
import az.fitnest.user.plan.nutrition.api.dto.response.NutritionPlanResponse;
import az.fitnest.user.plan.nutrition.api.dto.response.NutritionPlanResponses;
import az.fitnest.user.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/nutrition-plans")
@RequiredArgsConstructor
public class NutritionPlanController {

    private final NutritionPlansService nutritionPlansService;

    @GetMapping
    public ResponseEntity<ApiResponse<NutritionPlanResponses>> getNutritionPlans() {
        return ResponseEntity.ok(ApiResponse.success(nutritionPlansService.getNutritionPlans()));
    }

    @GetMapping("/{planId}")
    public ResponseEntity<ApiResponse<NutritionPlanResponse>> getNutritionPlan(@PathVariable Long planId) {
        return ResponseEntity.ok(ApiResponse.success(nutritionPlansService.getNutritionPlan(planId)));
    }

    @PostMapping("/{planId}/active")
    public ResponseEntity<ApiResponse<Void>> activateNutritionPlan(@PathVariable Long planId) {
        nutritionPlansService.activateNutritionPlan(planId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
