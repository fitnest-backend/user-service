package az.fitnest.user.user.api.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileImageRequest {

	@JsonProperty("image_url")
	@NotBlank(message = "Image URL is required")
	private String imageUrl;
}
