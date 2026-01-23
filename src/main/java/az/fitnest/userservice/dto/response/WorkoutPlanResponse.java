package az.fitnest.userservice.dto.response;

import lombok.Data;

@Data
public class WorkoutPlanResponse {

	private Long planId;

	private String title;

	private String description;

	private Boolean isActive;

}
