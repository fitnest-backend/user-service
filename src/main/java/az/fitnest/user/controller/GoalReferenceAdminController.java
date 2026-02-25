package az.fitnest.user.controller;

import az.fitnest.user.service.GoalReferenceService;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management Admin", description = "Administrative endpoints for managing wellness and fitness goal categories")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class GoalReferenceAdminController {

    private final GoalReferenceService goalReferenceService;

    @PostMapping
    @Operation(summary = "Create goal (Admin)", description = "Creates a new goal reference and initializes translations. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Goal created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Goal with this code already exists")
    })
    public ResponseEntity<ApiResponse<GoalReference>> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalReference goal = goalReferenceService.createGoal(request.getCode(), request.getTitle(), request.getSubtitle());
        return ResponseEntity.status(201).body(ApiResponse.success(goal));
    }

    @PutMapping("/{code}")
    @Operation(summary = "Update goal (Admin)", description = "Updates the title and subtitle of a goal reference. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<ApiResponse<GoalReference>> updateGoal(
            @PathVariable String code,
            @Valid @RequestBody az.fitnest.user.dto.request.UpdateGoalRequest request) {
        GoalReference goal = goalReferenceService.updateGoal(code, request.getTitle(), request.getSubtitle());
        return ResponseEntity.ok(ApiResponse.success(goal));
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Delete goal (Admin)", description = "Permanently deletes a goal reference and its image. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<ApiResponse<String>> deleteGoal(@PathVariable String code) {
        goalReferenceService.deleteGoal(code);
        return ResponseEntity.ok(ApiResponse.success("Goal reference deleted successfully"));
    }

    @PutMapping(path = "/{code}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload goal image (Admin)", description = "Uploads or replaces the image for a goal. Requires ADMIN role.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    })
    public ResponseEntity<Void> uploadGoalImage(
            @PathVariable String code,
            @Parameter(description = "Image file to upload (Max 5MB)") @RequestParam("file") MultipartFile file) {
        goalReferenceService.uploadGoalImage(code, file);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class CreateGoalRequest {
        @NotBlank private String code;
        @NotBlank private String title;
        private String subtitle;
    }
}
