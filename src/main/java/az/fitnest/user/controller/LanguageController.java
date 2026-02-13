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

    @Operation(summary = "Get all languages", description = "Returns a list of all languages.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Languages retrieved successfully",
                    content = @Content(schema = @Schema(implementation = LanguageDto.class)))
    })
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<LanguageDto>>> getAllLanguages() {
        return ResponseEntity.ok(ApiResponse.success(languageService.getAllLanguages()));
    }

    @Operation(summary = "Get language by ID", description = "Returns a language by its ID.")
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

    @Operation(summary = "Create a new language", description = "Creates a new language.")
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

    @Operation(summary = "Update a language", description = "Updates an existing language.")
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

    @Operation(summary = "Delete a language", description = "Deletes a language by its ID.")
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
