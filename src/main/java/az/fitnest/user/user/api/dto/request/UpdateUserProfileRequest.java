package az.fitnest.user.user.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

	@JsonProperty("full_name")
	@NotBlank(message = "Full name is required")
	@Size(min = 2, max = 120, message = "Full name must be between 2 and 120 characters")
	private String fullName;
	
	@Email(message = "Email must be valid")
	private String email;
	
}
