package az.fitnest.user.user.adapter.persistence;

import az.fitnest.user.user.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	
    @org.springframework.cache.annotation.Cacheable(value = "user_profiles", key = "#userId")
    Optional<UserProfile> findByUserId(Long userId);
}
