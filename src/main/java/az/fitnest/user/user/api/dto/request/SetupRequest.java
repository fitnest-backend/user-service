package az.fitnest.user.user.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetupRequest {

	@Valid
	@NotNull(message = "Profile is required")
	private ProfileInfo profile;

	@Data
	public static class ProfileInfo {
		@JsonProperty("height_cm")
		private Integer heightCm;

		@JsonProperty("weight_kg")
		private Double weightKg;

		private String gender;

		@JsonProperty("birth_date")
		private String birthDate;

		private String goal;
	}
}
