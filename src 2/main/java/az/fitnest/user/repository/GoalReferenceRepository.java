package az.fitnest.user.repository;

import az.fitnest.user.model.entity.GoalReference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalReferenceRepository extends JpaRepository<GoalReference, String> {
    List<GoalReference> findAllByOrderByGoalCodeAsc();
}
