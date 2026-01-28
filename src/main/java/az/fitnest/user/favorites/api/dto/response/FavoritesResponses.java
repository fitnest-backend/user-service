package az.fitnest.user.favorites.api.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FavoritesResponses {
	
	@JsonProperty("items")
	private List<FavoritesResponse> favorites;

}
