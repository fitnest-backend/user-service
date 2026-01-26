package az.fitnest.user.favorites.api.dto.request;

import lombok.Data;

@Data
public class FavoritesRequest {
	
	private Long entityId;
	
	private String entityType;

}
