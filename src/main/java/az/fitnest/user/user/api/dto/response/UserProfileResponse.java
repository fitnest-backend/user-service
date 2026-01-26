package az.fitnest.user.user.api.dto.response;

import lombok.Data;

@Data
public class UserProfileResponse {
	
	private Long userId;
	
	private String fullName;
	
	private String profileImageUrl;
	
	private boolean setupRequired;

}
