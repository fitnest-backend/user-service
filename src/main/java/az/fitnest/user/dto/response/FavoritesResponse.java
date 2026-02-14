package az.fitnest.user.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response containing favorite details")
public class FavoritesResponse {
	
	  @JsonProperty("favorite_id")
	  @Schema(description = "Unique identifier of the favorite", example = "67890")
	  private String favoriteId;

	  @JsonProperty("user_id")
	  @Schema(description = "ID of the user who favorited", example = "123")
	  private Long userId;
	  
	  @JsonProperty("entity_type")
	  @Schema(description = "Type of the favorited entity", example = "gym")
	  private String entityType;
	  
	  @JsonProperty("entity_id")
	  @Schema(description = "ID of the favorited entity", example = "12345")
	  private String entityId;

	  @JsonProperty("created_at")
	  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
	  @Schema(description = "Timestamp when the favorite was created", example = "15/01/2023 10:30:00")
	  private LocalDateTime createdAt;

}
