package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Request to add a favorite entity")
public class FavoritesRequest {
	
	@NotBlank
	@JsonProperty("entity_type")
	@Schema(description = "Type of the entity to favorite (e.g., gym, store)", example = "gym")
	private String entityType;

	@NotBlank
	@JsonProperty("entity_id")
	@Schema(description = "Unique identifier of the entity", example = "12345")
	private String entityId;

}
