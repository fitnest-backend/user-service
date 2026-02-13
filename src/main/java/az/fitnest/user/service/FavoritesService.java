package az.fitnest.user.service;

import az.fitnest.user.model.enums.EntityType;
import az.fitnest.user.dto.request.FavoritesRequest;
import az.fitnest.user.dto.response.FavoritesResponse;
import az.fitnest.user.dto.response.FavoritesResponses;

import java.util.List;
import java.util.Map;

public interface FavoritesService {
    Map<EntityType, Long> getFavoriteCounts(Long userId);
    FavoritesResponses getFavorites();
    FavoritesResponse addFavorites(FavoritesRequest request);
    void deleteFavorites(Long favoritesId);
    long countFavorites(Long userId, EntityType entityType);
    boolean isFavorited(Long userId, EntityType entityType, String entityId);
    Map<String, Boolean> bulkCheckFavorites(Long userId, EntityType entityType, List<String> entityIds);
}
