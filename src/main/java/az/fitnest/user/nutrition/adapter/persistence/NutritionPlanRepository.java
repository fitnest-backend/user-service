package az.fitnest.user.nutrition.adapter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import az.fitnest.user.nutrition.domain.model.NutritionPlan;

@Repository
public interface NutritionPlanRepository extends JpaRepository<NutritionPlan, Long>{

}
