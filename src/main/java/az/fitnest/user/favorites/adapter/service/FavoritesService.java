package az.fitnest.user.favorites.adapter.service;

import az.fitnest.user.favorites.api.dto.request.FavoritesRequest;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponse;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponses;
import az.fitnest.user.favorites.domain.enums.EntityType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class FavoritesService {

    public Map<EntityType, Long> getFavoriteCounts(Long userId) {
        return Collections.emptyMap();
    }

    public FavoritesResponses getFavorites() {
        FavoritesResponses resp = new FavoritesResponses();
        resp.setFavorites(Collections.emptyList());
        return resp;
    }

    public FavoritesResponse addFavorites(FavoritesRequest request) {
        return new FavoritesResponse();
    }

    public void deleteFavorites(Long favoritesId) {
    }

    public long countFavorites(Long userId, EntityType entityType) {
        return 0;
    }
}
