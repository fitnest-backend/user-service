package az.fitnest.userservice.service.impl;


import org.springframework.stereotype.Service;

import az.fitnest.userservice.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.entity.UserProfile;
import az.fitnest.userservice.exception.ResourceNotFoundException;
import az.fitnest.userservice.repository.GoalReferenceRepository;
import az.fitnest.userservice.repository.UserProfileRepository;
import az.fitnest.userservice.service.GoalReferenceInter;
import az.fitnest.userservice.util.UserContextUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoalReferenceService implements GoalReferenceInter {

	private final UserProfileRepository userProfileRepository;

	private final GoalReferenceRepository goalReferenceRepository;

	@Override
	public void updateMeGoals(UpdateGoalsRequest request) {
		Integer userId = UserContextUtil.getCurrentUserId();
		UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
			UserProfile newProfile = new UserProfile();
			newProfile.setUserId(userId);
			return newProfile;
		});

		boolean exists = goalReferenceRepository.existsByCode(request.getGoalCode());

		if (!exists) {
			throw new ResourceNotFoundException("Goal reference not found")
		}

		profile.setGoalCode(request.getGoalCode());
		userProfileRepository.save(profile);
	}

	@Override
	public void updateMeBody(UpdateBodyRequest request) {
		Integer userId = UserContextUtil.getCurrentUserId();
		UserProfile profile = userProfileRepository.findById(userId).orElseGet(() -> {
			UserProfile newProfile = new UserProfile();
			newProfile.setUserId(userId);
			return newProfile;
		});

		profile.setHeightCm(request.getHeightCm());
		profile.setWeightKg(request.getWeightKg());
		profile.setGender(request.getGender());
		profile.setBirthDate(request.getBirthDate());

		userProfileRepository.save(profile);

	}

}
