package az.fitnest.user.controller;

import az.fitnest.user.dto.request.CreateTranslationRequest;
import az.fitnest.user.model.entity.Translation;
import az.fitnest.user.repository.TranslationRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/translations")
@RequiredArgsConstructor
@Tag(name = "Translation Management", description = "Tərcümələri idarə etmək üçün ucluqlar")
public class TranslationController {

    private final TranslationRepository translationRepository;

    @Operation(summary = "Tərcümə yaradın və ya yeniləyin", description = "Verilmiş obyekt, dil və sahə üçün yeni tərcümə yaradır və ya mövcud olanı yeniləyir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tərcümə uğurla yaradıldı/yeniləndi",
                    content = @Content(schema = @Schema(implementation = Translation.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<Translation>> createOrUpdateTranslation(@RequestBody CreateTranslationRequest request) {
        Translation existing = translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                request.entityType(), request.entityId(), request.languageCode().toUpperCase(), request.fieldName()
        ).orElse(null);

        if (existing != null) {
            existing.setFieldValue(request.fieldValue());
            Translation saved = translationRepository.save(existing);
            return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(saved));
        } else {
            Translation translation = Translation.builder()
                    .entityType(request.entityType())
                    .entityId(request.entityId())
                    .languageCode(request.languageCode().toUpperCase())
                    .fieldName(request.fieldName())
                    .fieldValue(request.fieldValue())
                    .build();
            Translation saved = translationRepository.save(translation);
            return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(saved));
        }
    }

    @Operation(summary = "Obyekt üçün tərcümələri əldə edin", description = "Xüsusi obyekt növü və ID-si üçün bütün tərcümələri əldə edir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tərcümələr uğurla əldə edildi",
                    content = @Content(schema = @Schema(implementation = List.class)))
    })
    @GetMapping("/{entityType}/{entityId}")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<List<Translation>>> getTranslations(@PathVariable String entityType, @PathVariable String entityId) {
        List<Translation> translations = translationRepository.findByEntityTypeAndEntityId(entityType, entityId);
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(translations));
    }

    @GetMapping("/goals")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<List<Translation>>> getGoalTranslations() {
        List<Translation> translations = translationRepository.findByEntityType("GoalReference");
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(translations));
    }

    @GetMapping("/gender")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<List<Translation>>> getGenderTranslations() {
        List<Translation> translations = translationRepository.findByEntityType("Gender");
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(translations));
    }

    @GetMapping("/bmi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<List<Translation>>> getBmiTranslations() {
        List<Translation> translations = translationRepository.findByEntityType("Message");
        return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(translations));
    }

    @Operation(summary = "Tərcüməni silin", description = "Xüsusi tərcüməni ID vasitəsilə silir.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tərcümə uğurla silindi")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        translationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
