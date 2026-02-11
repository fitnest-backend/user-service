package az.fitnest.user.controller;

import az.fitnest.user.repository.GoalReferenceRepository;
import az.fitnest.user.entity.GoalReference;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing goal reference data.
 * Provides endpoints for creating and managing fitness goals that users can select.
 */
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "Endpoints for managing goal references")
@SecurityRequirement(name = "bearerAuth")
public class GoalReferenceController {

    private final GoalReferenceRepository goalReferenceRepository;

    @Operation(summary = "Get all goal references", description = "Returns a list of all fitness goal references.")
    @GetMapping
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<java.util.List<GoalReference>>> getAllGoals() {
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.findAllByOrderByGoalCodeAsc()));
    }

    @Operation(summary = "Get goal reference by code", description = "Returns details of a specific goal reference.")
    @GetMapping("/{code}")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> getGoalByCode(@PathVariable String code) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goal));
    }

    @Operation(summary = "Create a new goal reference", description = "Creates a new goal reference.")
    @PostMapping
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalReference goal = new GoalReference();
        goal.setGoalCode(request.getCode());
        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    @Operation(summary = "Update a goal reference", description = "Updates an existing goal reference.")
    @PutMapping("/{code}")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> updateGoal(
            @PathVariable String code,
            @Valid @RequestBody az.fitnest.user.dto.UpdateGoalRequest request) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));
        
        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());
        
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    @Operation(summary = "Delete a goal reference", description = "Deletes a goal reference.")
    @DeleteMapping("/{code}")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<Void>> deleteGoal(@PathVariable String code) {
        if (!goalReferenceRepository.existsById(code)) {
            throw new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code);
        }
        goalReferenceRepository.deleteById(code);
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(null));
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
