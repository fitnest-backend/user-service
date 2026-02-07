package az.fitnest.user.favorites.adapter.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import az.fitnest.user.favorites.adapter.persistence.FavoritesRepository;
import az.fitnest.user.favorites.api.dto.request.FavoritesRequest;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponse;
import az.fitnest.user.favorites.api.dto.response.FavoritesResponses;
import az.fitnest.user.favorites.domain.enums.EntityType;
import az.fitnest.user.favorites.domain.model.Favorite;
import az.fitnest.user.shared.exception.AlreadyFavoritedException;
import az.fitnest.user.shared.exception.BadRequestException;
import az.fitnest.user.shared.exception.FavoriteNotFoundException;
import az.fitnest.user.shared.util.UserContext;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritesService {
	
	private final FavoritesRepository favoritesRepository;
	
	public FavoritesResponses getFavorites() {
		Long userId = UserContext.getCurrentUserId();
		
		var items = favoritesRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
				.map(this::toResponse)
				.toList();
		
		FavoritesResponses resp = new FavoritesResponses();
		resp.setFavorites(items);
		return resp;
	}

	public FavoritesResponse addFavorites(FavoritesRequest request) {
		Long userId = UserContext.getCurrentUserId();
		
		EntityType entityType = parseEntityType(request.getEntityType());
		Long entityId = request.getEntityId();
		
		if (favoritesRepository.existsByUserIdAndEntityTypeAndEntityId(userId, entityType, entityId)) {
			throw new AlreadyFavoritedException("Already favorited");
		}
		
		Favorite favorite = new Favorite();
		favorite.setUserId(userId);
		favorite.setEntityType(entityType);
		favorite.setEntityId(entityId);
		favorite.setCreatedAt(LocalDateTime.now());
		
		Favorite saved = favoritesRepository.save(favorite);
		return toResponse(saved);
		
	}

	public void deleteFavorites(Long favoritesId) {
		Long userId = UserContext.getCurrentUserId();
		
		Favorite favorite = favoritesRepository.findByFavoriteIdAndUserId(favoritesId, userId)
				.orElseThrow(() -> new FavoriteNotFoundException("Favorite not found"));
		
		favoritesRepository.delete(favorite);
		
	}
	
	public long countFavorites(Long userId, EntityType entityType) {
		return favoritesRepository.countByUserIdAndEntityType(userId, entityType);
	}
	
	private EntityType parseEntityType(String raw) {
		if (raw == null) {
			throw new BadRequestException("entity_type is required");
		}
		try {
			return EntityType.valueOf(raw.trim().toUpperCase());
		} catch (IllegalArgumentException ex) {
			throw new BadRequestException("Invalid entity_type. Allowed: gym, store");
		}
	}
	
	private FavoritesResponse toResponse(Favorite favorite) {
		FavoritesResponse resp = new FavoritesResponse();
		resp.setFavoriteId(favorite.getFavoriteId());
		resp.setUserId(favorite.getUserId());
		resp.setEntityId(favorite.getEntityId());
		resp.setEntityType(favorite.getEntityType() != null ? favorite.getEntityType().name().toLowerCase() : null);
		resp.setCreatedAt(favorite.getCreatedAt());
		return resp;
	}

}
