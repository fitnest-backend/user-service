package az.fitnest.user.goal.adapter.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import az.fitnest.user.user.api.dto.request.UpdateBodyRequest;
import az.fitnest.user.goal.api.dto.request.UpdateGoalsRequest;
import az.fitnest.user.goal.api.dto.response.GoalItemResponse;
import az.fitnest.user.goal.api.dto.response.GoalsResponse;
import az.fitnest.user.goal.domain.model.GoalReference;
import az.fitnest.user.user.domain.model.UserProfile;
import az.fitnest.user.shared.exception.ResourceNotFoundException;
import az.fitnest.user.goal.adapter.persistence.GoalReferenceRepository;
import az.fitnest.user.user.adapter.persistence.UserProfileRepository;
import az.fitnest.user.shared.util.UserContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalReferenceService {

	private final UserProfileRepository userProfileRepository;

	private final GoalReferenceRepository goalReferenceRepository;

	public void updateMeGoals(UpdateGoalsRequest request) {
		Long userId = UserContext.getCurrentUserId();
		UserProfile profile = userProfileRepository.findByUserId(userId).orElseGet(() -> {
			UserProfile newProfile = new UserProfile();
			newProfile.setUserId(userId);
			return newProfile;
		});

		boolean exists = goalReferenceRepository.existsByGoalCode(request.getGoalCode());

		if (!exists) {
			throw new ResourceNotFoundException("Goal reference not found");
		}

		profile.setGoalCode(request.getGoalCode());
		userProfileRepository.save(profile);
	}

	public void updateMeBody(UpdateBodyRequest request) {
		Long userId = UserContext.getCurrentUserId();
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
		List<GoalItemResponse> items = goalReferenceRepository.findAllByOrderByGoalCodeAsc().stream()
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
