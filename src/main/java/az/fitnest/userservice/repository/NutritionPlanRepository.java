package az.fitnest.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.userservice.entity.NutritionPlan;

@Repository
public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Long>{

}
