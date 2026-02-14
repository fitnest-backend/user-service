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
@Tag(name = "Translation Management", description = "Endpoints for managing translations")
public class TranslationController {

    private final TranslationRepository translationRepository;

    @Operation(summary = "Create or update a translation", description = "Creates a new translation or updates an existing one for the given entity, language, and field.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translation created/updated successfully",
                    content = @Content(schema = @Schema(implementation = Translation.class)))
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<az.fitnest.user.dto.ApiResponse<Translation>> createOrUpdateTranslation(@RequestBody CreateTranslationRequest request) {
        Translation existing = translationRepository.findByEntityTypeAndEntityIdAndLanguageCodeAndFieldName(
                request.getEntityType(), request.getEntityId(), request.getLanguageCode().toUpperCase(), request.getFieldName()
        ).orElse(null);

        if (existing != null) {
            existing.setFieldValue(request.getFieldValue());
            Translation saved = translationRepository.save(existing);
            return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(saved));
        } else {
            Translation translation = Translation.builder()
                    .entityType(request.getEntityType())
                    .entityId(request.getEntityId())
                    .languageCode(request.getLanguageCode().toUpperCase())
                    .fieldName(request.getFieldName())
                    .fieldValue(request.getFieldValue())
                    .build();
            Translation saved = translationRepository.save(translation);
            return ResponseEntity.ok(az.fitnest.user.dto.ApiResponse.success(saved));
        }
    }

    @Operation(summary = "Get translations for an entity", description = "Retrieves all translations for a specific entity type and ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translations retrieved successfully",
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

    @Operation(summary = "Delete a translation", description = "Deletes a specific translation by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Translation deleted successfully")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTranslation(@PathVariable Long id) {
        translationRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
