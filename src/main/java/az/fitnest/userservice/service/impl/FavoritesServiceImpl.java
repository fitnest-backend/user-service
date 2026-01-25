package az.fitnest.userservice.service.impl;

import org.springframework.stereotype.Service;

import az.fitnest.userservice.request.FavoritesRequest;
import az.fitnest.userservice.response.FavoritesResponses;
import az.fitnest.userservice.service.FavoritesService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoritesServiceImpl implements FavoritesService{
	
	
	
	
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
