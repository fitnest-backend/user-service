package az.fitnest.user.favorites.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.user.favorites.domain.model.Favorite;
import az.fitnest.user.favorites.domain.enums.EntityType;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorite, Long>{

    boolean existsByUserIdAndEntityTypeAndEntityId(Long userId, EntityType entityType, String entityId);

    long countByUserIdAndEntityType(Long userId, EntityType entityType);

    @org.springframework.data.jpa.repository.Query("SELECT f.entityType, COUNT(f) FROM Favorite f WHERE f.userId = :userId GROUP BY f.entityType")
    List<Object[]> countAllByUserIdGroupByEntityType(@org.springframework.data.repository.query.Param("userId") Long userId);

    List<Favorite> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Favorite> findByFavoriteIdAndUserId(Long favoriteId, Long userId);

    List<Favorite> findByUserIdAndEntityTypeAndEntityIdIn(Long userId, EntityType entityType, List<String> entityIds);
}
