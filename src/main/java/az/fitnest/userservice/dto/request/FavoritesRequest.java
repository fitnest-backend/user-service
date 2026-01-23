package az.fitnest.userservice.dto.request;

import lombok.Data;

@Data
public class FavoritesRequest {
	
	private Long entityId;
	
	private String entityType;

}
