package az.fitnest.userservice.service;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;

public interface GoalsService {
	
	void updateMeGoals(UpdateGoalsRequest request);
	
	void updateMeBody(UpdateBodyRequest request);
	

}
