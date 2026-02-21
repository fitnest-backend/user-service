package az.fitnest.user.controller;

import az.fitnest.user.service.GoalReferenceService;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.dto.response.GoalItemResponse;
import az.fitnest.user.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "Endpoints for managing wellness and fitness goal categories (e.g., Lose Weight, Build Muscle).")
@SecurityRequirement(name = "bearerAuth")
public class GoalReferenceController {

    private final GoalReferenceService goalReferenceService;

    // --- Public & User Endpoints ---

    @GetMapping("/goals")
    @Operation(summary = "Get all goal references", description = "Retrieves a list of all available goals, translated to the user's preferred language.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goals retrieved successfully", content = @Content(schema = @Schema(implementation = GoalItemResponse.class)))
    })
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<List<GoalItemResponse>>> getAllGoals() {
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceService.getAllGoals()));
    }

    @GetMapping("/goals/{code}")
    @Operation(summary = "Get goal by code", description = "Retrieves a specific goal by its unique code.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalItemResponse>> getGoalByCode(
            @Parameter(description = "Unique code of the goal (e.g., LOSE_WEIGHT)") @PathVariable String code) {
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceService.getGoalByCode(code)));
    }

    @GetMapping(value = "/goals/images/{fsId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Stream goal image", description = "Streams the image file associated with a goal from storage.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Image stream started"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Image not found")
    })
    public ResponseEntity<StreamingResponseBody> streamGoalImage(@PathVariable String fsId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(goalReferenceService.streamGoalImage(fsId));
    }

    // --- Admin Endpoints ---

    @PostMapping("/admin/goals")
    @Operation(summary = "Create goal (Admin)", description = "Creates a new goal reference and initializes translations. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Goal created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Goal with this code already exists")
    })
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> createGoal(@Valid @RequestBody CreateGoalRequest request) {
        GoalReference goal = goalReferenceService.createGoal(request.getCode(), request.getTitle(), request.getSubtitle());
        return ResponseEntity.status(201).body(az.fitnest.user.dto.ApiResponse.success(goal));
    }

    @PutMapping("/admin/goals/{code}")
    @Operation(summary = "Update goal (Admin)", description = "Updates the title and subtitle of a goal reference. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> updateGoal(
            @PathVariable String code,
            @Valid @RequestBody az.fitnest.user.dto.request.UpdateGoalRequest request) {
        GoalReference goal = goalReferenceService.updateGoal(code, request.getTitle(), request.getSubtitle());
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goal));
    }

    @DeleteMapping("/admin/goals/{code}")
    @Operation(summary = "Delete goal (Admin)", description = "Permanently deletes a goal reference and its image. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Goal deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Goal not found")
    })
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<String>> deleteGoal(@PathVariable String code) {
        goalReferenceService.deleteGoal(code);
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success("Goal reference deleted successfully"));
    }

    @PutMapping(path = "/admin/goals/{code}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload goal image (Admin)", description = "Uploads or replaces the image for a goal. Requires ADMIN role.")
    @PreAuthorize("hasRole('ADMIN')")
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
