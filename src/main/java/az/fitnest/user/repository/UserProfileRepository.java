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
    long countByUserIdIn(List<Long> userIds);

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
        SELECT u.userId FROM UserProfile u
        WHERE (:userId IS NOT NULL AND u.userId = :userId)
           OR LOWER(u.firstName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))
    """)
    List<Long> searchIdsByQuery(@Param("query") String query, @Param("userId") Long userId);

    @Query("""
        SELECT u FROM UserProfile u
        WHERE u.userId IN :userIds
           OR (:userId IS NOT NULL AND u.userId = :userId)
           OR LOWER(u.firstName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT(:query, '%'))
           OR LOWER(u.email) LIKE LOWER(CONCAT(:query, '%'))
    """)
    Page<UserProfile> searchByQueryOrUserIds(@Param("query") String query, @Param("userId") Long userId, @Param("userIds") List<Long> userIds, Pageable pageable);

    @Query(value = """
        WITH current_month AS (
            SELECT count(*) AS cnt FROM users 
            WHERE status = 'ACTIVE' AND created_at >= date_trunc('month', CURRENT_DATE)
        ),
        previous_month AS (
            SELECT count(*) AS cnt FROM users 
            WHERE status = 'ACTIVE' 
              AND created_at >= date_trunc('month', CURRENT_DATE - INTERVAL '1 month')
              AND created_at < date_trunc('month', CURRENT_DATE)
        )
        SELECT 
            (SELECT cnt FROM current_month) AS currentTotal,
            CASE WHEN (SELECT cnt FROM previous_month) = 0 THEN 0.0
                 ELSE ((SELECT cnt FROM current_month) - (SELECT cnt FROM previous_month))::numeric / (SELECT cnt FROM previous_month) * 100.0
            END AS percentageChange
        """, nativeQuery = true)
    KpiProjection getActiveCustomersKpi();

    interface KpiProjection {
        Long getCurrentTotal();
        Double getPercentageChange();
    }

    interface UserIdNameProjection {
        Long getUserId();
        String getFirstName();
        String getLastName();
    }

    @Query("SELECT u.userId as userId, u.firstName as firstName, u.lastName as lastName FROM UserProfile u WHERE u.userId IN :userIds")
    List<UserIdNameProjection> findNamesByUserIds(@Param("userIds") List<Long> userIds);
}
