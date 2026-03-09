package az.fitnest.user.controller;

import az.fitnest.user.dto.response.PaginatedResponse;
import az.fitnest.user.dto.response.RecentSearchDto;
import az.fitnest.user.service.RecentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recent-searches")
@RequiredArgsConstructor
@Tag(name = "Recent Searches", description = "İstifadəçilərin son axtarış tarixçəsini idarə etmək üçün ucluqlar.")
public class RecentSearchController {

    private final RecentSearchService recentSearchService;

    @Operation(summary = "Son axtarışları əldə edin", description = "İstifadəçinin son axtarışlarını səhifələnmiş formada əldə edir.")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Axtarışlar uğurla əldə edildi"),
            @ApiResponse(responseCode = "401", description = "Autentifikasiya tələb olunur")
    })
    @GetMapping
    public ResponseEntity<PaginatedResponse<RecentSearchDto>> getRecentSearches(
            @Parameter(description = "Axtarış növü (GYM, STORE və ya ALL)") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "Səhifə indeksi (1-dən başlayaraq)") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "Hər səhifədəki elementlərin sayı") @RequestParam(defaultValue = "10") int page_size,
            @Parameter(description = "Çeşidləmə qaydası (asc, desc)") @RequestParam(value = "sort_dir", defaultValue = "desc") String sortDir) {
        Long userId = getCurrentUserId();
        return ResponseEntity.ok(recentSearchService.getRecentSearches(userId, type, page, page_size, sortDir));
    }

    @Operation(summary = "Axtarışı yadda saxlayın", description = "Yeni axtarış sorğusunu istifadəçinin son axtarış tarixçəsinə əlavə edir.")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Axtarış uğurla saxlanıldı"),
            @ApiResponse(responseCode = "401", description = "Autentifikasiya tələb olunur")
    })
    @PostMapping
    public ResponseEntity<Void> saveRecentSearch(
            @RequestParam("type") String type,
            @RequestParam("query") String query) {
        if (type == null || type.isEmpty()) {
            throw new IllegalArgumentException("Type is required for saving recent searches.");
        }
        Long userId = getCurrentUserId();
        recentSearchService.saveSearch(userId, query, type);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Xüsusi axtarışı silin", description = "İstifadəçinin keçmişindən xüsusi bir axtarış sorğusunu silir.")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping
    public ResponseEntity<Void> deleteSearch(
            @Parameter(description = "Axtarış növü (GYM, STORE və ya ALL)") @RequestParam("type") String type,
            @Parameter(description = "Silinəcək axtarış sorğusu") @RequestParam("query") String query) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            recentSearchService.deleteSearch(userId, type.toUpperCase(), query);
        }
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Bütün axtarış tarixçəsini təmizləyin", description = "Seçilmiş növ üçün bütün axtarış tarixçəsini təmizləyir.")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/all")
    public ResponseEntity<Void> clearAllSearches(
            @Parameter(description = "Axtarış növü (GYM, STORE və ya ALL)") @RequestParam("type") String type) {
        Long userId = getCurrentUserId();
        if (userId != null) {
            recentSearchService.clearAllSearches(userId, type.toUpperCase());
        }
        return ResponseEntity.noContent().build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long) {
            return (Long) auth.getPrincipal();
        }
        return null;
    }
}
