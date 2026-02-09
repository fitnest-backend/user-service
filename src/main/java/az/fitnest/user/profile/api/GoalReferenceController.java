package az.fitnest.user.profile.api;

import az.fitnest.user.profile.adapter.persistence.GoalReferenceRepository;
import az.fitnest.user.profile.domain.model.GoalReference;
import az.fitnest.user.shared.dto.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/goals")
@RequiredArgsConstructor
public class GoalReferenceController {

    private final GoalReferenceRepository goalReferenceRepository;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<GoalReference>> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalReference goal = new GoalReference();
        goal.setGoalCode(request.getCode());
        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());
        
        return ResponseEntity.ok(ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    @Data
    public static class CreateGoalRequest {
        @NotBlank
        private String code;
        @NotBlank
        private String title;
        private String subtitle;
    }
}
