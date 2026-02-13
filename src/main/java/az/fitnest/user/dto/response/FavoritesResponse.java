package az.fitnest.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FavoritesResponse {
	
	  @JsonProperty("favorite_id")
	  private String favoriteId;

	  @JsonProperty("user_id")
	  private Long userId;
	  
	  @JsonProperty("entity_type")
	  private String entityType;
	  
	  @JsonProperty("entity_id")
	  private String entityId;

	  @JsonProperty("created_at")
	  private LocalDateTime createdAt;

}
