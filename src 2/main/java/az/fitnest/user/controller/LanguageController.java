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
@Tag(name = "Languages", description = "Endpoints for managing languages")
public class LanguageController {

    private final LanguageService languageService;

    @Operation(summary = "Get all languages", description = "Retrieves a complete list of all supported languages in the system. This endpoint is restricted to administrators and provides language codes and names for configuration purposes.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Languages retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LanguageDto>>> getAllLanguages() {
        return ResponseEntity.ok(ApiResponse.success(languageService.getAllLanguages()));
    }

    @Operation(summary = "Get language by ID", description = "Retrieves the details of a specific language using its unique ID. This endpoint is useful for administrators to view individual language configurations.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Language not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LanguageDto>> getLanguageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(languageService.getLanguageById(id)));
    }

    @Operation(summary = "Create a new language", description = "Adds a new language to the system with the provided code and name. This allows administrators to expand the list of supported languages for user preferences.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Language created successfully",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<LanguageDto>> createLanguage(@Valid @RequestBody LanguageCreateRequest request) {
        return ResponseEntity.status(201).body(ApiResponse.success(languageService.createLanguage(request)));
    }

    @Operation(summary = "Update a language", description = "Modifies the code and name of an existing language. Only the provided fields will be updated, allowing partial updates. This helps administrators correct or update language information.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language updated successfully",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Language not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<LanguageDto>> updateLanguage(@PathVariable Long id, @Valid @RequestBody LanguageCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(languageService.updateLanguage(id, request)));
    }

    @Operation(summary = "Delete a language", description = "Removes a language from the system using its unique ID. This action is permanent and should be used carefully as it may affect users who have selected this language. Restricted to administrators.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Language deleted successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Language not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLanguage(@PathVariable Long id) {
        languageService.deleteLanguage(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
