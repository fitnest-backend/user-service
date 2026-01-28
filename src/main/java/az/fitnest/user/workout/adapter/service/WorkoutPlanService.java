package az.fitnest.user.workout.adapter.service;

import org.springframework.stereotype.Service;

import az.fitnest.user.workout.api.dto.response.WorkoutPlanResponse;
import az.fitnest.user.workout.api.dto.response.WorkoutPlanResponses;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WorkoutPlanService {
	
	public WorkoutPlanResponses getWorkoutPlans() {
		return null;
	}

	public WorkoutPlanResponse getWorkoutPlan(Long planId) {
		return null;
	}

	public void getActiveWorkoutPlan(Long planId) {
		
	}

}
