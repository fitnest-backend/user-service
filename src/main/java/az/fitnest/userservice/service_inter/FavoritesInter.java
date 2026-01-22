package az.fitnest.userservice.service_inter;

import az.fitnest.userservice.request.FavoritesRequest;
import az.fitnest.userservice.response.FavoritesResponses;

public interface FavoritesInter {
	
	FavoritesResponses getFavorites();
	
	void addFavorites(FavoritesRequest request);
	
	void deleteFavorites(Long favoritesId);

}
