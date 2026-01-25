package az.fitnest.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.userservice.entity.GoalReference;

@Repository
public interface GoalReferenceRepository extends JpaRepository<GoalReference, String>{

	boolean existsByCode(String goalCode);

}
