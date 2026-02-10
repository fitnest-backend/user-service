package az.fitnest.user.favorites.adapter.service;

import az.fitnest.user.favorites.adapter.persistence.FavoritesRepository;
import az.fitnest.user.favorites.api.dto.request.FavoritesRequest;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponse;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponses;
import az.fitnest.user.favorites.domain.enums.EntityType;
import az.fitnest.user.favorites.domain.model.Favorite;
import az.fitnest.user.shared.exception.ConflictException;
import az.fitnest.user.shared.exception.ResourceNotFoundException;
import az.fitnest.user.shared.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;

    public Map<EntityType, Long> getFavoriteCounts(Long userId) {
        List<Object[]> counts = favoritesRepository.countAllByUserIdGroupByEntityType(userId);
        Map<EntityType, Long> result = new HashMap<>();
        for (Object[] row : counts) {
            result.put((EntityType) row[0], (Long) row[1]);
        }
        return result;
    }

    public FavoritesResponses getFavorites() {
        Long userId = UserContext.getCurrentUserId();
        List<Favorite> favorites = favoritesRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        
        FavoritesResponses resp = new FavoritesResponses();
        resp.setFavorites(favorites.stream()
                .map(this::toResponse)
                .collect(Collectors.toList()));
        return resp;
    }

    @Transactional
    public FavoritesResponse addFavorites(FavoritesRequest request) {
        Long userId = UserContext.getCurrentUserId();
        
        EntityType type;
        try {
            type = EntityType.valueOf(request.getEntityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new az.fitnest.user.shared.exception.BadRequestException("INVALID_ENTITY_TYPE");
        }

        if (favoritesRepository.existsByUserIdAndEntityTypeAndEntityId(userId, type, request.getEntityId())) {
            throw new ConflictException("ALREADY_FAVORITED");
        }

        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setEntityType(type);
        favorite.setEntityId(request.getEntityId());
        favorite.setCreatedAt(LocalDateTime.now());

        Favorite saved = favoritesRepository.save(favorite);
        return toResponse(saved);
    }

    @Transactional
    public void deleteFavorites(Long favoritesId) {
        Long userId = UserContext.getCurrentUserId();
        Favorite favorite = favoritesRepository.findByFavoriteIdAndUserId(favoritesId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("FAVORITE_NOT_FOUND"));
        
        favoritesRepository.delete(favorite);
    }

    public long countFavorites(Long userId, EntityType entityType) {
        return favoritesRepository.countByUserIdAndEntityType(userId, entityType);
    }

    public boolean isFavorited(Long userId, EntityType entityType, Long entityId) {
        if (userId == null) return false;
        return favoritesRepository.existsByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId);
    }

    private FavoritesResponse toResponse(Favorite favorite) {
        FavoritesResponse response = new FavoritesResponse();
        response.setFavoriteId(favorite.getFavoriteId());
        response.setUserId(favorite.getUserId());
        response.setEntityType(favorite.getEntityType().name());
        response.setEntityId(favorite.getEntityId());
        response.setCreatedAt(favorite.getCreatedAt());
        return response;
    }
}
