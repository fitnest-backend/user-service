package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FavoritesRequest {
	
	@NotBlank
	@JsonProperty("entity_type")
	private String entityType;

	@NotBlank
	@JsonProperty("entity_id")
	private String entityId;

}
