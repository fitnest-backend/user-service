package az.fitnest.user.plan.nutrition.adapter.service;

import az.fitnest.user.plan.nutrition.api.dto.response.NutritionPlanResponse;
import az.fitnest.user.plan.nutrition.api.dto.response.NutritionPlanResponses;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class NutritionPlansService {

    public NutritionPlanResponses getNutritionPlans() {
        return NutritionPlanResponses.builder()
                .nutritionPlans(Collections.emptyList())
                .build();
    }

    public NutritionPlanResponse getNutritionPlan(Long planId) {
        return null;
    }

    public void activateNutritionPlan(Long planId) {
        // Implementation pending
    }
}
