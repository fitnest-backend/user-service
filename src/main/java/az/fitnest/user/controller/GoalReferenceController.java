package az.fitnest.user.controller;

import az.fitnest.user.repository.GoalReferenceRepository;
import az.fitnest.user.model.entity.GoalReference;
import az.fitnest.user.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import az.fitnest.user.exception.ConflictException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import az.fitnest.user.exception.BadRequestException;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "Endpoints for managing goal references")
@SecurityRequirement(name = "bearerAuth")
public class GoalReferenceController {

    private final GoalReferenceRepository goalReferenceRepository;
    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Get all goal references",
            description = "Retrieves a comprehensive list of all predefined fitness and health goal references available in the system. The goals are ordered alphabetically by their unique code for easy reference and selection."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal references retrieved successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "[{\"goalCode\": \"WEIGHT_LOSS\", \"title\": \"Lose Weight\", \"subtitle\": \"Burn fat and achieve your ideal weight\"}, {\"goalCode\": \"MUSCLE_GAIN\", \"title\": \"Gain Muscle\", \"subtitle\": \"Build strength and muscle mass\"}]"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            )
    })
    @GetMapping
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<java.util.List<GoalReference>>> getAllGoals() {
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.findAllByOrderByGoalCodeAsc()));
    }

    @Operation(
            summary = "Get goal reference by code",
            description = "Retrieves detailed information about a specific fitness or health goal reference using its unique code identifier. This endpoint is useful for displaying goal details in user interfaces."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal reference retrieved successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "{\"goalCode\": \"WEIGHT_LOSS\", \"title\": \"Lose Weight\", \"subtitle\": \"Burn fat and achieve your ideal weight\"}"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal reference not found with the provided code",
                    content = @Content
            )
    })
    @GetMapping("/{code}")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> getGoalByCode(@PathVariable String code) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goal));
    }

    @Operation(
            summary = "Create a new goal reference",
            description = "Creates a new fitness or health goal reference in the system. This endpoint is restricted to administrators and requires a unique goal code. The goal will be available for users to select as their fitness objective."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal reference created successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "{\"goalCode\": \"WEIGHT_LOSS\", \"title\": \"Lose Weight\", \"subtitle\": \"Burn fat and achieve your ideal weight\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin role required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflict - goal code already exists",
                    content = @Content
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> createGoal(
            @Valid @ModelAttribute CreateGoalRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        if (goalReferenceRepository.existsById(request.getCode())) {
            throw new ConflictException("Goal already exists: " + request.getCode());
        }
        GoalReference goal = new GoalReference();
        goal.setGoalCode(request.getCode());
        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());

        if (image != null && !image.isEmpty()) {
            validateImage(image);
            String imageUrl = fileStorageService.saveFile(image);
            goal.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    @Operation(
            summary = "Update a goal reference",
            description = "Updates the title and subtitle of an existing fitness or health goal reference. Only the provided fields will be updated, leaving others unchanged. This endpoint is restricted to administrators."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal reference updated successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "{\"goalCode\": \"WEIGHT_LOSS\", \"title\": \"Lose Weight\", \"subtitle\": \"Burn fat and achieve your ideal weight\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin role required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal reference not found with the provided code",
                    content = @Content
            )
    })
    @PutMapping(value = "/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<GoalReference>> updateGoal(
            @PathVariable String code,
            @Valid @ModelAttribute az.fitnest.user.dto.request.UpdateGoalRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));

        goal.setTitle(request.getTitle());
        goal.setSubtitle(request.getSubtitle());

        if (image != null && !image.isEmpty()) {
            // Delete old image if exists
            if (goal.getImageUrl() != null && !goal.getImageUrl().isBlank()) {
                try {
                    fileStorageService.deleteFile(goal.getImageUrl());
                } catch (Exception e) {
                    // log or ignore
                }
            }
            validateImage(image);
            String imageUrl = fileStorageService.saveFile(image);
            goal.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(goalReferenceRepository.save(goal)));
    }

    @Operation(
            summary = "Delete a goal reference",
            description = "Permanently removes a fitness or health goal reference from the system using its unique code. This action cannot be undone and may affect users who have selected this goal. Restricted to administrators only."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Goal reference deleted successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "null"))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin role required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal reference not found with the provided code",
                    content = @Content
            )
    })
    @DeleteMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<Void>> deleteGoal(@PathVariable String code) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));

        // Delete associated image if exists
        if (goal.getImageUrl() != null && !goal.getImageUrl().isBlank()) {
            try {
                fileStorageService.deleteFile(goal.getImageUrl());
            } catch (Exception e) {
                // log or ignore
            }
        }

        goalReferenceRepository.deleteById(code);
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(null));
    }

    @Operation(
            summary = "Upload an image for a goal reference",
            description = "Uploads an image file for a specific goal reference, which can be used for display in user interfaces. The image is associated with the goal reference and can be updated or removed later."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Image uploaded successfully",
                    content = @Content(schema = @Schema(implementation = az.fitnest.user.dto.ApiResponse.class), examples = @ExampleObject(value = "{\"goalCode\": \"WEIGHT_LOSS\", \"imagePath\": \"/images/goals/weight_loss.jpg\"}"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data or validation failed",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - authentication required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - admin role required",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Goal reference not found with the provided code",
                    content = @Content
            )
    })
    @PostMapping(value = "/{code}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<String>> uploadGoalImage(
            @PathVariable String code,
            @RequestParam("file") MultipartFile file) {
        GoalReference goal = goalReferenceRepository.findById(code)
                .orElseThrow(() -> new az.fitnest.user.exception.ResourceNotFoundException("Goal not found: " + code));

        validateImage(file);
        String imageUrl = fileStorageService.saveFile(file);

        // Update the goal reference with the new image url
        goal.setImageUrl(imageUrl);
        goalReferenceRepository.save(goal);

        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(imageUrl));
    }

    private void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            throw new BadRequestException("File size exceeds 5MB");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Only image files are allowed");
        }
    }

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
