package az.fitnest.user.profile.api;

import az.fitnest.user.profile.adapter.persistence.GoalReferenceRepository;
import az.fitnest.user.profile.domain.model.GoalReference;
import az.fitnest.user.shared.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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

/**
 * Admin controller for managing goal reference data.
 * Provides endpoints for creating and managing fitness goals that users can select.
 */
@RestController
@RequestMapping("/api/v1/admin/goals")
@RequiredArgsConstructor
@Tag(name = "Admin Goal Management", description = "Admin endpoints for managing goal references")
@SecurityRequirement(name = "bearerAuth")
public class GoalReferenceController {

    private final GoalReferenceRepository goalReferenceRepository;

    @Operation(
            summary = "Create a new goal reference",
            description = "Creates a new goal reference that users can select as their fitness goal. " +
                    "Requires admin privileges."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Goal created successfully",
                    content = @Content(schema = @Schema(implementation = GoalReference.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Admin role required",
                    content = @Content
            )
    })
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<az.fitnest.user.shared.dto.ApiResponse<GoalReference>> createGoal(
            @Valid @RequestBody CreateGoalRequest request) {
        GoalReference goal = new GoalReference();
        goal.setGoalCode(request.getCode());
        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());
        
        return ResponseEntity.ok(az.fitnest.user.shared.dto.ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    /**
     * Request DTO for creating a new goal reference.
     */
    @Data
    @Schema(description = "Request to create a new goal reference")
    public static class CreateGoalRequest {
        @NotBlank
        @Schema(description = "Unique code for the goal", example = "WEIGHT_LOSS")
        private String code;

        @NotBlank
        @Schema(description = "Display title for the goal", example = "Lose Weight")
        private String title;

        @Schema(description = "Optional subtitle or description", example = "Burn fat and achieve your ideal weight")
        private String subtitle;
    }
}
