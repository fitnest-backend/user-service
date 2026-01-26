package az.fitnest.user.user.adapter.persistence;

import az.fitnest.user.user.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long>{
	
    Optional<UserProfile> findByUserId(Long userId);
}
