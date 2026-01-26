package az.fitnest.userservice.favorites.api.dto.response;

import java.time.LocalDateTime;

import az.fitnest.userservice.enums.EntityType;
import lombok.Data;

@Data
public class FavoritesResponse {
	
	  private Long favoriteId;
	  
	  private Long userId;
	  
	  private EntityType entityType;
	  
	  private Long entityId;
	  
	  private LocalDateTime createdAt;

}
