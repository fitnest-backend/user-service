package az.fitnest.userservice.dto.response;

import lombok.Data;

@Data
public class SummaryResponse {
	
	private UserProfileResponse user;
	
	private CountersResponse counters;

}
