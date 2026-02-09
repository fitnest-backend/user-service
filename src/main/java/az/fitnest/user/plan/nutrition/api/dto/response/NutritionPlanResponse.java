package az.fitnest.user.plan.nutrition.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NutritionPlanResponse {
    private Long planId;
    private String title;
    private String description;
    private Boolean isActive;
}
