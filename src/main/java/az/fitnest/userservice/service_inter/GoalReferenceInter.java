package az.fitnest.userservice.service_inter;

import az.fitnest.userservice.request.UpdateBodyRequest;
import az.fitnest.userservice.request.UpdateGoalsRequest;

public interface GoalReferenceInter {
	
	void updateMeGoals(UpdateGoalsRequest request);
	
	void updateMeBody(UpdateBodyRequest request);
	

}
