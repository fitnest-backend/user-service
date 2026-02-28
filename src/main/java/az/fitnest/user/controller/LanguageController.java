package az.fitnest.user.controller;

import az.fitnest.user.dto.ApiResponse;
import az.fitnest.user.dto.request.LanguageCreateRequest;
import az.fitnest.user.dto.response.LanguageDto;
import az.fitnest.user.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/languages")
@RequiredArgsConstructor
@Tag(name = "Languages", description = "Dilləri idarə etmək üçün ucluqlar")
public class LanguageController {

    private final LanguageService languageService;

    @Operation(summary = "Bütün dilləri əldə edin", description = "Sistemdə dəstəklənən bütün dillərin tam siyahısını əldə edir. Bu ucluq administratorlar üçün məhdudlaşdırılıb və konfiqurasiya məqsədləri üçün dil kodlarını və adlarını təqdim edir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dillər uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LanguageDto>>> getAllLanguages() {
        return ResponseEntity.ok(ApiResponse.success(languageService.getAllLanguages()));
    }

    @Operation(summary = "Dili kod vasitəsilə əldə edin", description = "Unikal kodu vasitəsilə xüsusi dilin təfərrüatlarını əldə edir. Bu ucluq administratorlar üçün fərdi dil konfiqurasiyalarına baxmaq üçün faydalıdır.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dil uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dil tapılmadı")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{code}")
    public ResponseEntity<ApiResponse<LanguageDto>> getLanguageByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(languageService.getLanguageByCode(code)));
    }

    @Operation(summary = "Yeni dil yaradın", description = "Təqdim olunan kod və adla sistemə yeni dil əlavə edir. Bu, administratorlara istifadəçi seçimləri üçün dəstəklənən dillərin siyahısını genişləndirməyə imkan verir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Dil uğurla yaradıldı",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yanlış sorğu məlumatı")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<LanguageDto>> createLanguage(@Valid @RequestBody LanguageCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(languageService.createLanguage(request)));
    }

    @Operation(summary = "Dili yeniləyin", description = "Mövcud dilin kodunu və adını dəyişdirir. Yalnız təqdim olunan sahələr yenilənəcək, bu da qismən yeniləmələrə imkan verir. Bu, administratorlara dil məlumatlarını düzəltməyə və ya yeniləməyə kömək edir.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dil uğurla yeniləndi",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dil tapılmadı"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yanlış sorğu məlumatı")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{code}")
    public ResponseEntity<ApiResponse<LanguageDto>> updateLanguage(@PathVariable String code, @Valid @RequestBody LanguageCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(languageService.updateLanguage(code, request)));
    }

    @Operation(summary = "Dili silin", description = "Unikal ID vasitəsilə dili sistemdən silir. Bu hərəkət daimidir və ehtiyatla istifadə edilməlidir, çünki bu dili seçmiş istifadəçilərə təsir göstərə bilər. Administratorlar üçün məhdudlaşdırılıb.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Dil uğurla silindi"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Dil tapılmadı")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{code}")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(@PathVariable String code) {
        languageService.deleteLanguage(code);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
