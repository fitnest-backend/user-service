package az.fitnest.userservice.service;

import az.fitnest.userservice.dto.response.NutritionPlanResponse;
import az.fitnest.userservice.dto.response.NutritionPlanResponses;


public interface NutritionPlansService {

	NutritionPlanResponses getNutritionPlans();

	NutritionPlanResponse getNutritionPlan(Long planId);

	void getActiveNutritionPlan(Long planId);

}
