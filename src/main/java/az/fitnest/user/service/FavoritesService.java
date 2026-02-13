package az.fitnest.user.service;

import az.fitnest.user.constants.EntityType;
import az.fitnest.user.dto.FavoritesRequest;
import az.fitnest.user.dto.FavoritesResponse;
import az.fitnest.user.dto.FavoritesResponses;
import az.fitnest.user.model.entity.Favorite;
import az.fitnest.user.exception.ConflictException;
import az.fitnest.user.exception.ResourceNotFoundException;
import az.fitnest.user.repository.FavoritesRepository;
import az.fitnest.user.util.UserContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface FavoritesService {
    Map<EntityType, Long> getFavoriteCounts(Long userId);
    FavoritesResponses getFavorites();
    FavoritesResponse addFavorites(FavoritesRequest request);
    void deleteFavorites(Long favoritesId);
    long countFavorites(Long userId, EntityType entityType);
    boolean isFavorited(Long userId, EntityType entityType, String entityId);
    Map<String, Boolean> bulkCheckFavorites(Long userId, EntityType entityType, List<String> entityIds);
}
