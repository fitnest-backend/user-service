package az.fitnest.user.service;

import az.fitnest.user.repository.FavoritesRepository;
import az.fitnest.user.dto.FavoritesRequest;
import az.fitnest.user.dto.FavoritesResponse;
import az.fitnest.user.dto.FavoritesResponses;
import az.fitnest.user.constants.EntityType;
import az.fitnest.user.entity.Favorite;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.util.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing user favorites.
 * Handles adding, removing, and querying favorite entities such as gyms and stores.
 *
 * <p>This service supports:
 * <ul>
 *   <li>Adding favorites with duplicate prevention</li>
 *   <li>Removing favorites</li>
 *   <li>Listing all favorites for a user</li>
 *   <li>Bulk checking favorite status</li>
 *   <li>Publishing favorite events to Kafka</li>
 * </ul>
 *
 * @see Favorite
 * @see EntityType
 */
@Service
@RequiredArgsConstructor
public class FavoritesService {

    private final FavoritesRepository favoritesRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

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

        return toResponse(saved);
    }

    @Transactional
    public void deleteFavorites(Long favoritesId) {
        Long userId = UserContext.getCurrentUserId();
        Favorite favorite = favoritesRepository.findByFavoriteIdAndUserId(favoritesId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("FAVORITE_NOT_FOUND"));
        
        // Publish event before deletion
        publishFavoriteEvent("FAVORITE_REMOVED", userId, favorite.getEntityType(), favorite.getEntityId());

        favoritesRepository.delete(favorite);
    }

    public long countFavorites(Long userId, EntityType entityType) {
        return favoritesRepository.countByUserIdAndEntityType(userId, entityType);
    }

    public boolean isFavorited(Long userId, EntityType entityType, String entityId) {
        if (userId == null) return false;
        return favoritesRepository.existsByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId);
    }

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
