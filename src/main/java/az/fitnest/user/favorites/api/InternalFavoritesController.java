package az.fitnest.user.favorites.api;

import az.fitnest.user.favorites.adapter.service.FavoritesService;
import az.fitnest.user.favorites.domain.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/internal/favorites")
@RequiredArgsConstructor
public class InternalFavoritesController {

    private final FavoritesService favoritesService;

    @GetMapping("/check")
    public boolean isFavorited(
            @RequestParam("userId") Long userId,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("entityId") String entityId) {
        return favoritesService.isFavorited(userId, entityType, entityId);
    }

    @PostMapping("/bulk-check")
    public Map<String, Boolean> bulkCheckFavorites(
            @RequestParam("userId") Long userId,
            @RequestParam("entityType") EntityType entityType,
            @RequestBody List<String> entityIds) {
        return favoritesService.bulkCheckFavorites(userId, entityType, entityIds);
    }
}
