package az.fitnest.user.service.impl;
import az.fitnest.user.service.*;

import az.fitnest.user.repository.FavoritesRepository;
import az.fitnest.user.dto.request.FavoritesRequest;
import az.fitnest.user.dto.response.FavoritesResponse;
import az.fitnest.user.dto.response.FavoritesResponses;
import az.fitnest.user.model.enums.EntityType;
import az.fitnest.user.model.entity.Favorite;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TranslationService translationService;

        @Override
    public Map<EntityType, Long> getFavoriteCounts(Long userId) {
        List<Object[]> counts = favoritesRepository.countAllByUserIdGroupByEntityType(userId);
        Map<EntityType, Long> result = new HashMap<>();
        for (Object[] row : counts) {
            result.put((EntityType) row[0], (Long) row[1]);
        }
        return result;
    }

        @Override
    public FavoritesResponses getFavorites(String language) {
        Long userId = UserContext.getCurrentUserId();
        List<Favorite> favorites = favoritesRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
        
        FavoritesResponses resp = new FavoritesResponses();
        resp.setFavorites(favorites.stream()
                .map(favorite -> toResponse(favorite, language))
                .collect(Collectors.toList()));
        return resp;
    }

    @CacheEvict(value="user_summaries", key="T(az.fitnest.user.util.UserContext).getCurrentUserId()")
    @Transactional
        @Override
    public FavoritesResponse addFavorites(FavoritesRequest request, String language) {
        Long userId = UserContext.getCurrentUserId();
        
        EntityType type;
        try {
            type = EntityType.valueOf(request.getEntityType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new az.fitnest.user.exception.BadRequestException("INVALID_ENTITY_TYPE");
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

        // Publish event for cache invalidation
        publishFavoriteEvent("FAVORITE_ADDED", userId, type, request.getEntityId());

        return toResponse(saved, language);
    }

    @CacheEvict(value="user_summaries", key="T(az.fitnest.user.util.UserContext).getCurrentUserId()")
    @Transactional
        @Override
    public void deleteFavorites(Long favoritesId) {
        Long userId = UserContext.getCurrentUserId();
        Favorite favorite = favoritesRepository.findByFavoriteIdAndUserId(favoritesId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("FAVORITE_NOT_FOUND"));
        
        // Publish event before deletion
        publishFavoriteEvent("FAVORITE_REMOVED", userId, favorite.getEntityType(), favorite.getEntityId());

        favoritesRepository.delete(favorite);
    }

        @Override
    public long countFavorites(Long userId, EntityType entityType) {
        return favoritesRepository.countByUserIdAndEntityType(userId, entityType);
    }

        @Override
    public boolean isFavorited(Long userId, EntityType entityType, String entityId) {
        if (userId == null) return false;
        return favoritesRepository.existsByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId);
    }

        @Override
    public Map<String, Boolean> bulkCheckFavorites(Long userId, EntityType entityType, List<String> entityIds) {
        if (userId == null || entityIds == null || entityIds.isEmpty()) {
            return entityIds.stream().collect(Collectors.toMap(id -> id, id -> false));
        }

        List<Favorite> favorites = favoritesRepository.findByUserIdAndEntityTypeAndEntityIdIn(userId, entityType, entityIds);
        Set<String> favoritedIds = favorites.stream()
                .map(Favorite::getEntityId)
                .collect(Collectors.toSet());

        return entityIds.stream()
                .collect(Collectors.toMap(id -> id, favoritedIds::contains));
    }

    private FavoritesResponse toResponse(Favorite favorite) {
        FavoritesResponse response = new FavoritesResponse();
        response.setFavoriteId("f_" + favorite.getFavoriteId());
        response.setEntityType(favorite.getEntityType().name().toLowerCase());
        response.setEntityId(favorite.getEntityId());
        response.setCreatedAt(favorite.getCreatedAt());
        return response;
    }

    private FavoritesResponse toResponse(Favorite favorite, String language) {
        FavoritesResponse response = new FavoritesResponse();
        response.setFavoriteId("f_" + favorite.getFavoriteId());
        String translatedEntityType = translationService.getTranslatedValue("EntityType", favorite.getEntityType().name(), "label", language);
        response.setEntityType(translatedEntityType);
        response.setEntityId(favorite.getEntityId());
        response.setCreatedAt(favorite.getCreatedAt());
        return response;
    }

    private void publishFavoriteEvent(String eventType, Long userId, EntityType entityType, String entityId) {
        Map<String, Object> event = Map.of(
            "eventType", eventType,
            "userId", userId,
            "entityType", entityType.name(),
            "entityId", entityId,
            "timestamp", System.currentTimeMillis()
        );
        kafkaTemplate.send("favorites-events", event);
    }
}
