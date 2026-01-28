package az.fitnest.user.favorites.api.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FavoritesResponse {
	
	  @JsonProperty("favorite_id")
	  private Long favoriteId;
	  
	  @JsonProperty("user_id")
	  private Long userId;
	  
	  @JsonProperty("entity_type")
	  private String entityType;
	  
	  @JsonProperty("entity_id")
	  private Long entityId;
	  
	  @JsonProperty("created_at")
	  private LocalDateTime createdAt;

}
