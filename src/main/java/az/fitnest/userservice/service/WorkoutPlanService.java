package az.fitnest.userservice.service;

import az.fitnest.userservice.response.WorkoutPlanResponse;
import az.fitnest.userservice.response.WorkoutPlanResponses;

public interface WorkoutPlanService {
	
	WorkoutPlanResponses getWorkoutPlans();
	
	WorkoutPlanResponse getWorkoutPlan(Long planId);
	
	void getActiveWorkoutPlan(Long planId);
	
	

}
