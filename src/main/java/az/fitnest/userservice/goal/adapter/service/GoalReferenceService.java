package az.fitnest.userservice.goal.adapter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.fitnest.userservice.user.api.dto.request.UpdateBodyRequest;
import az.fitnest.userservice.goal.api.dto.request.UpdateGoalsRequest;
import az.fitnest.userservice.goal.api.dto.response.GoalItemResponse;
import az.fitnest.userservice.goal.api.dto.response.GoalsResponse;
import az.fitnest.userservice.goal.domain.model.GoalReference;
import az.fitnest.userservice.user.domain.model.UserProfile;
import az.fitnest.userservice.shared.exception.ResourceNotFoundException;
import az.fitnest.userservice.goal.adapter.persistence.GoalReferenceRepository;
import az.fitnest.userservice.user.adapter.persistence.UserProfileRepository;
import az.fitnest.userservice.shared.util.UserContextUtil;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalReferenceService {

	private final UserProfileRepository userProfileRepository;

	private final GoalReferenceRepository goalReferenceRepository;

	public void updateMeGoals(UpdateGoalsRequest request) {
		Long userId = UserContextUtil.getCurrentUserId();
		UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
			UserProfile newProfile = new UserProfile();
			newProfile.setUserId(userId);
			return newProfile;
		});

		boolean exists = goalReferenceRepository.existsByCode(request.getGoalCode());

		if (!exists) {
			throw new ResourceNotFoundException("Goal reference not found");
		}

		profile.setGoalCode(request.getGoalCode());
		userProfileRepository.save(profile);
	}

	public void updateMeBody(UpdateBodyRequest request) {
		Long userId = UserContextUtil.getCurrentUserId();
		UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
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

	@Transactional
	public GoalsResponse getGoals() {
		List<GoalItemResponse> items = goalReferenceRepository.findAllByOrderByCodeAsc().stream()
				.map(this::toGoalItemResponse)
				.toList();
		return GoalsResponse.builder()
				.items(items)
				.build();
	}

	private GoalItemResponse toGoalItemResponse(GoalReference goalReference) {
		return GoalItemResponse.builder()
				.code(goalReference.getGoalCode())
				.title(goalReference.getTitle())
				.subtitle(goalReference.getSubtitle())
				.build();
	}

}
