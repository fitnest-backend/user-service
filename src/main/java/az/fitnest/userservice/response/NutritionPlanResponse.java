package az.fitnest.userservice.response;

import lombok.Data;

@Data
public class NutritionPlanResponse {

	private Long planId;

	private String title;

	private String description;

	private Boolean isActive;

}
