package az.fitnest.userservice.service_inter;

import az.fitnest.userservice.response.NutritionPlanResponse;
import az.fitnest.userservice.response.NutritionPlanResponses;


public interface NutritionPlansInter {

	NutritionPlanResponses getNutritionPlans();

	NutritionPlanResponse getNutritionPlan(Long planId);

	void getActiveNutritionPlan(Long planId);

}
