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
@Tag(name = "Goal Management Admin", description = "Sağlamlıq və fitnes hədəf kateqoriyalarını idarə etmək üçün administrativ ucluqlar")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class GoalReferenceAdminController {

    private final GoalReferenceService goalReferenceService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Hədəf yaradın (Admin)", description = "Yeni hədəf arayışı yaradır və tərcümələri hazırlayır. Şəkil yüklənə bilər. ADMIN rolu tələb olunur.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Hədəf uğurla yaradıldı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Bu kodlu hədəf artıq mövcuddur")
    })
    public ResponseEntity<ApiResponse<GoalReference>> createGoal(
            @RequestParam("code") @NotBlank @jakarta.validation.constraints.Pattern(regexp = "^[A-Z0-9_-]+$", message = "error.invalid_goal_code") String code,
            @RequestParam("title") @NotBlank String title,
            @RequestParam(value = "subtitle", required = false) String subtitle,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        GoalReference goal = goalReferenceService.createGoal(code, title, subtitle, image);
        return ResponseEntity.status(201).body(ApiResponse.success(goal));
    }

    @PutMapping(path = "/{code}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Hədəfi yeniləyin (Admin)", description = "Hədəf arayışının başlığını, yarımbaşlığını və şəklini yeniləyir. ADMIN rolu tələb olunur.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf uğurla yeniləndi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Hədəf tapılmadı")
    })
    public ResponseEntity<ApiResponse<GoalReference>> updateGoal(
            @PathVariable String code,
            @RequestParam("title") @NotBlank String title,
            @RequestParam(value = "subtitle", required = false) String subtitle,
            @RequestParam(value = "image", required = false) MultipartFile image) {
        GoalReference goal = goalReferenceService.updateGoal(code, title, subtitle, image);
        return ResponseEntity.ok(ApiResponse.success(goal));
    }

    @DeleteMapping("/{code}")
    @Operation(summary = "Hədəfi silin (Admin)", description = "Hədəf arayışını və onun şəklini həmişəlik silir. ADMIN rolu tələb olunur.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf uğurla silindi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Hədəf tapılmadı")
    })
    public ResponseEntity<ApiResponse<String>> deleteGoal(@PathVariable String code) {
        goalReferenceService.deleteGoal(code);
        return ResponseEntity.ok(ApiResponse.success("Goal reference deleted successfully"));
    }
}
