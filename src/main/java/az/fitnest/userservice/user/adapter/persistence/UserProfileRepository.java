package az.fitnest.userservice.user.adapter.persistence;

import az.fitnest.userservice.user.domain.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer>{
	
    Optional<UserProfile> findByUserId(Long userId);
}
