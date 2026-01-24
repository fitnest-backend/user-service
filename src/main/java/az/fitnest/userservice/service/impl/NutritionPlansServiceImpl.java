package az.fitnest.userservice.service.impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.dto.response.NutritionPlanResponse;
import az.fitnest.userservice.dto.response.NutritionPlanResponses;
import az.fitnest.userservice.service.NutritionPlansService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NutritionPlansServiceImpl implements NutritionPlansService{
	
	
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
