package az.fitnest.user.favorites.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.user.favorites.domain.model.Favorite;
import az.fitnest.user.favorites.domain.enums.EntityType;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Long>{

    boolean existsByUserIdAndEntityTypeAndEntityId(Long userId, EntityType entityType, Long entityId);

    long countByUserIdAndEntityType(Long userId, EntityType entityType);

    List<Favorite> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByFavoriteIdAndUserId(Long favoriteId, Long userId);
}
