package az.fitnest.userservice.request;

import lombok.Data;

@Data
public class FavoritesRequest {
	
	private Long entityId;
	
	private String entityType;

}
