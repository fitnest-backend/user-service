package az.fitnest.userservice.user.api.dto.request;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {

	
	private String fullName;
	
	private String email;
	
	private String language;
	
}
