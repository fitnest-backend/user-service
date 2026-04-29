package az.fitnest.user.repository;

import az.fitnest.user.model.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);
    Page<UserProfile> findAllByUserIdIn(List<Long> userIds, Pageable pageable);

    // NOTE: For best performance, ensure there are indexes on firstName, lastName, and email columns.
    // Prefix search (LIKE :query%) allows index usage. Full name search is omitted for index efficiency.
    @Query("""
        SELECT u FROM UserProfile u
        WHERE (:userId IS NOT NULL AND u.userId = :userId)
           OR LOWER(u.firstName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))
    """)
    Page<UserProfile> searchByQuery(@Param("query") String query, @Param("userId") Long userId, Pageable pageable);

    @Query("""
        SELECT u FROM UserProfile u
        WHERE u.userId IN :userIds
           OR (:userId IS NOT NULL AND u.userId = :userId)
           OR LOWER(u.firstName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))
    """)
    Page<UserProfile> searchByQueryOrUserIds(@Param("query") String query, @Param("userId") Long userId, @Param("userIds") List<Long> userIds, Pageable pageable);
}
