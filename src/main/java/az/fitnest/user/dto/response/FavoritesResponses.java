package az.fitnest.user.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Response containing a list of user favorites")
public class FavoritesResponses {
	
	@JsonProperty("items")
	@Schema(description = "List of favorite items")
	private List<FavoritesResponse> favorites;

}
