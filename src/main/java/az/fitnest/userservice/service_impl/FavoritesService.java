package az.fitnest.userservice.service_impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.request.FavoritesRequest;
import az.fitnest.userservice.response.FavoritesResponses;
import az.fitnest.userservice.service_inter.FavoritesInter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritesService implements FavoritesInter{
	
	
	
	
	@Override
	public FavoritesResponses getFavorites() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addFavorites(FavoritesRequest request) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteFavorites(Long favoritesId) {
		// TODO Auto-generated method stub
		
	}

}
