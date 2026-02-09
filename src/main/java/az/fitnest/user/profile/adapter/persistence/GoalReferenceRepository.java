package az.fitnest.user.profile.adapter.persistence;

import az.fitnest.user.profile.domain.model.GoalReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalReferenceRepository extends JpaRepository<GoalReference, String> {
    List<GoalReference> findAllByOrderByGoalCodeAsc();
}
