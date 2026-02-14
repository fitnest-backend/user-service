package az.fitnest.user.controller;

import az.fitnest.user.service.FavoritesService;
import az.fitnest.user.model.enums.EntityType;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/favorites")
@RequiredArgsConstructor
@Hidden // Hide from Swagger - internal endpoints only
public class InternalFavoritesController {

    private final FavoritesService favoritesService;

    @Operation(
            summary = "Check if entity is favorited",
            description = "Internal service endpoint to verify if a specific entity (such as a gym or store) has been favorited by the given user. Returns a boolean indicating the favorite status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Favorite status returned",
                    content = @Content(schema = @Schema(implementation = Boolean.class))
            )
    })
    @GetMapping("/check")
    public boolean isFavorited(
            @Parameter(description = "User ID to check") @RequestParam("userId") Long userId,
            @Parameter(description = "Type of entity (GYM, STORE, etc.)") @RequestParam("entityType") EntityType entityType,
            @Parameter(description = "Entity identifier") @RequestParam("entityId") String entityId) {
        return favoritesService.isFavorited(userId, entityType, entityId);
    }

    @Operation(
            summary = "Bulk check favorite status",
            description = "Internal service endpoint for efficiently checking favorite status across multiple entities of the same type for a user. Accepts a list of entity IDs and returns a map with each ID mapped to its favorite boolean status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Bulk favorite status returned as map",
                    content = @Content(schema = @Schema(implementation = Map.class))
            )
    })
    @PostMapping("/bulk-check")
    public Map<String, Boolean> bulkCheckFavorites(
            @Parameter(description = "User ID to check") @RequestParam("userId") Long userId,
            @Parameter(description = "Type of entities") @RequestParam("entityType") EntityType entityType,
            @Parameter(description = "List of entity identifiers") @RequestBody List<String> entityIds) {
        return favoritesService.bulkCheckFavorites(userId, entityType, entityIds);
    }
}
