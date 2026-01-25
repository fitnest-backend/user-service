package az.fitnest.userservice.service_impl;


import org.springframework.stereotype.Service;

import az.fitnest.userservice.entity.UserProfile;
import az.fitnest.userservice.exception.CustomException;
import az.fitnest.userservice.repository.GoalReferenceRepository;
import az.fitnest.userservice.repository.UserProfileRepository;
import az.fitnest.userservice.request.UpdateBodyRequest;
import az.fitnest.userservice.request.UpdateGoalsRequest;
import az.fitnest.userservice.service_inter.GoalReferenceInter;
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
			throw new CustomException("Goal reference not found", null, null, 404, null);
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
