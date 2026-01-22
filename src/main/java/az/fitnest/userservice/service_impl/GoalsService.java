package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.request.UpdateBodyRequest;
import az.fitnest.userservice.request.UpdateGoalsRequest;
import az.fitnest.userservice.service_inter.GoalsInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalsService implements GoalsInter{
	
	@Override
	public void updateMeGoals(UpdateGoalsRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateMeBody(UpdateBodyRequest request) {
		// TODO Auto-generated method stub
		
	}

}
