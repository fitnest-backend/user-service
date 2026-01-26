package az.fitnest.userservice.user.api.dto.response;

import lombok.Data;

@Data
public class SummaryResponse {
	
	private UserProfileResponse user;
	
	private CountersResponse counters;

}
