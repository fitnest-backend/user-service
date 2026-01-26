package az.fitnest.user.user.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
	
	@JsonProperty("user_id")
	private String userId;
	
	@JsonProperty("full_name")
	private String fullName;
	
	private String mobile;
	
	private String email;
	
	@JsonProperty("profile_image_url")
	private String profileImageUrl;
	
	@JsonProperty("created_at")
	private LocalDateTime createdAt;

}
