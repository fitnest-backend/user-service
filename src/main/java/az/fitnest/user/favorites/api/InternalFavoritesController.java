package az.fitnest.user.favorites.api;

import az.fitnest.user.favorites.adapter.service.FavoritesService;
import az.fitnest.user.favorites.domain.enums.EntityType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/internal/favorites")
@RequiredArgsConstructor
public class InternalFavoritesController {

    private final FavoritesService favoritesService;

    @GetMapping("/check")
    public boolean isFavorited(
            @RequestParam("userId") Long userId,
            @RequestParam("entityType") EntityType entityType,
            @RequestParam("entityId") Long entityId) {
        return favoritesService.isFavorited(userId, entityType, entityId);
    }
}
