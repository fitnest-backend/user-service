package az.fitnest.userservice.service;

import az.fitnest.userservice.response.NutritionPlanResponse;
import az.fitnest.userservice.response.NutritionPlanResponses;


public interface NutritionPlansService {

	NutritionPlanResponses getNutritionPlans();

	NutritionPlanResponse getNutritionPlan(Long planId);

	void getActiveNutritionPlan(Long planId);

}
