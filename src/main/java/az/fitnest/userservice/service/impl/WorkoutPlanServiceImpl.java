package az.fitnest.userservice.service.impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.response.WorkoutPlanResponse;
import az.fitnest.userservice.response.WorkoutPlanResponses;
import az.fitnest.userservice.service.WorkoutPlanService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutPlanServiceImpl implements WorkoutPlanService{
	
	@Override
	public WorkoutPlanResponses getWorkoutPlans() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WorkoutPlanResponse getWorkoutPlan(Long planId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getActiveWorkoutPlan(Long planId) {
		// TODO Auto-generated method stub
		
	}

}
