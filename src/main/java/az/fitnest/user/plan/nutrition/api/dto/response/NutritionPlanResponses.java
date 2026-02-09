package az.fitnest.user.plan.nutrition.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionPlanResponses {
    private List<NutritionPlanResponse> nutritionPlans;
}
