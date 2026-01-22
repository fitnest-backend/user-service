package az.fitnest.userservice.service_inter;

import az.fitnest.userservice.response.WorkoutPlanResponse;
import az.fitnest.userservice.response.WorkoutPlanResponses;

public interface WorkoutPlanInter {
	
	WorkoutPlanResponses getWorkoutPlans();
	
	WorkoutPlanResponse getWorkoutPlan(Long planId);
	
	void getActiveWorkoutPlan(Long planId);
	
	

}
