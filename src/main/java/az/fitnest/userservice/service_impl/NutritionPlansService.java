package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.response.NutritionPlanResponse;
import az.fitnest.userservice.response.NutritionPlanResponses;
import az.fitnest.userservice.service_inter.NutritionPlansInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NutritionPlansService implements NutritionPlansInter{
	
	
	@Override
	public NutritionPlanResponses getNutritionPlans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public NutritionPlanResponse getNutritionPlan(Long planId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getActiveNutritionPlan(Long planId) {
		// TODO Auto-generated method stub
		
	}

}
