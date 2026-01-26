package az.fitnest.userservice.favorites.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.favorites.api.dto.request.FavoritesRequest;
import az.fitnest.userservice.favorites.api.dto.response.FavoritesResponses;
import az.fitnest.userservice.favorites.adapter.service.FavoritesService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class FavoritesController {

	private final FavoritesService favoritesService;

	@GetMapping("/favorites")
	public ResponseEntity<FavoritesResponses> getFavorites() {
		FavoritesResponses favorites = favoritesService.getFavorites();
		return ResponseEntity.status(HttpStatus.OK).body(favorites);
	}

	@PostMapping("/favorites")
	public ResponseEntity<Void> addFavorite(@RequestBody FavoritesRequest request) {
		favoritesService.addFavorites(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/favorites/{favoritesId}")
	public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoritesId) {
		favoritesService.deleteFavorites(favoritesId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
