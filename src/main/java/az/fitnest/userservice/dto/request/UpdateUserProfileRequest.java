package az.fitnest.userservice.dto.request;

import lombok.Data;

@Data
public class UpdateUserProfileRequest {

	
	private String fullName;
	
	private String email;
	
	private String language;
	
}
