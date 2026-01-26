package az.fitnest.userservice.goal.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.userservice.goal.domain.model.GoalReference;

import java.util.List;

@Repository
public interface GoalReferenceRepository extends JpaRepository<GoalReference, String>{

	boolean existsByCode(String goalCode);

	List<GoalReference> findAllByOrderByCodeAsc();

}
