package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.response.WorkoutPlanResponse;
import az.fitnest.userservice.response.WorkoutPlanResponses;
import az.fitnest.userservice.service_inter.WorkoutPlanInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService implements WorkoutPlanInter{
	
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
