package az.fitnest.user.user.adapter.persistence;

import az.fitnest.user.user.domain.model.UserLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLocationRepository extends JpaRepository<UserLocation, Long> {
}

