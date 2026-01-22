package az.fitnest.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.userservice.entity.GoalReference;

@Repository
public interface GoalsRepository extends JpaRepository<GoalReference, String>{

}
