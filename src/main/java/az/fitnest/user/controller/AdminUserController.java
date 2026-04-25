package az.fitnest.user.controller;

import az.fitnest.user.dto.PaginatedResponse;
import az.fitnest.user.dto.response.AdminUserResponse;
import az.fitnest.user.dto.response.UserStatisticsResponse;
import az.fitnest.user.service.AdminUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Admin Controller", description = "Endpoints for user management and statistics")
@SecurityRequirement(name = "bearerAuth")
public class AdminUserController {

    private final AdminUserService adminUserService;

    @Operation(
        summary = "Get all users with details",
        description = "Returns a paginated list of users including status and subscription info. Supports search by user ID, full name, email, or phone number."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<PaginatedResponse<AdminUserResponse>> getAllUsers(
            @Parameter(description = "Page number (0-indexed)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Filter by subscription package ID")
            @RequestParam(required = false) Long packageId,
            @Parameter(description = "Filter by package duration in months", example = "1")
            @RequestParam(required = false) Integer packageDuration,
            @Parameter(description = "Filter by subscription status",
                    schema = @Schema(allowableValues = {"ACTIVE", "FINISHED", "FROZEN", "LAST_7_DAYS"}))
            @RequestParam(required = false) String subscriptionStatus,
            @Parameter(description = "Sort order for the results. Values: "
                    + "newest - Newly added, "
                    + "name_asc - Name : A-Z, "
                    + "name_desc - Name : Z-A, "
                    + "finishDate_asc - Subscription end date (soonest first), "
                    + "finishDate_desc - Subscription end date (latest first), "
                    + "registrationDate_desc - Registration date (new → old), "
                    + "registrationDate_asc - Registration date (old → new)",
                    schema = @Schema(allowableValues = {"newest", "name_asc", "name_desc",
                            "finishDate_asc", "finishDate_desc",
                            "registrationDate_desc", "registrationDate_asc"}))
            @RequestParam(required = false) String sort,
            @Parameter(description = "Search by user ID, full name, email, or phone number")
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(adminUserService.getAllUsers(PageRequest.of(page, size), packageId, packageDuration, subscriptionStatus, sort, search));
    }

    @Operation(
        summary = "Get user statistics",
        description = "Returns counts for total users, active/frozen, finished and soon to expire subscriptions."
    )
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics")
    public ResponseEntity<UserStatisticsResponse> getUserStatistics() {
        return ResponseEntity.ok(adminUserService.getUserStatistics());
    }
}
