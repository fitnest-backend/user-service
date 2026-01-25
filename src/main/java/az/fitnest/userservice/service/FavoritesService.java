package az.fitnest.userservice.service;

import az.fitnest.userservice.request.FavoritesRequest;
import az.fitnest.userservice.response.FavoritesResponses;

public interface FavoritesService {
	
	FavoritesResponses getFavorites();
	
	void addFavorites(FavoritesRequest request);
	
	void deleteFavorites(Long favoritesId);

}
