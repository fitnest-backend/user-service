package az.fitnest.user.user.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupResponse {

	@JsonProperty("setup_required")
	private Boolean setupRequired;

	private UserInfo user;

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class UserInfo {
		@JsonProperty("user_id")
		private String userId;

		private String language;

		private ProfileInfo profile;

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
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
}
