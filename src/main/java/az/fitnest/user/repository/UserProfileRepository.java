package az.fitnest.user.repository;

import az.fitnest.user.model.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);
    org.springframework.data.domain.Page<UserProfile> findAllByUserIdIn(java.util.List<Long> userIds, org.springframework.data.domain.Pageable pageable);
}
