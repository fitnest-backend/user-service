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
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "Sağlamlıq və fitnes hədəf kateqoriyalarına baxmaq üçün ucluqlar.")
@SecurityRequirement(name = "bearerAuth")
public class GoalReferenceController {

    private final GoalReferenceService goalReferenceService;

    @GetMapping
    @Operation(summary = "Bütün hədəf arayışlarını əldə edin", description = "Mövcud olan bütün hədəflərin siyahısını əldə edir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəflər uğurla əldə edildi", content = @Content(schema = @Schema(implementation = GoalItemResponse.class)))
    })
    public ResponseEntity<ApiResponse<List<GoalItemResponse>>> getAllGoals() {
        return ResponseEntity.ok(ApiResponse.success(goalReferenceService.getAllGoals()));
    }

    @GetMapping("/{code}")
    @Operation(summary = "Hədəfi kod vasitəsilə əldə edin", description = "Unikal kodu vasitəsilə xüsusi hədəfi əldə edir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Hədəf tapıldı",
                    content = @Content(schema = @Schema(implementation = GoalItemResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Hədəf tapılmadı",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ApiResponse<GoalItemResponse>> getGoalByCode(
            @Parameter(description = "Hədəfin unikal kodu (məsələn, LOSE_WEIGHT)") @PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(goalReferenceService.getGoalByCode(code)));
    }

    @GetMapping(value = "/images/{fsId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    @Operation(summary = "Hədəf şəklini yayımlayın", description = "Hədəf ilə əlaqəli şəkil faylını yaddaşdan yayımlayır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Şəkil yayımı başladı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Şəkil tapılmadı")
    })
    public ResponseEntity<StreamingResponseBody> streamGoalImage(@PathVariable String fsId) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=31536000, immutable")
                .body(goalReferenceService.streamGoalImage(fsId));
    }
}
