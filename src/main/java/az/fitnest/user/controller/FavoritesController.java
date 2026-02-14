package az.fitnest.user.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.dto.request.FavoritesRequest;
import az.fitnest.user.dto.response.FavoritesResponse;
import az.fitnest.user.dto.response.FavoritesResponses;
import az.fitnest.user.service.FavoritesService;
import az.fitnest.user.util.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
@Tag(name = "Favorites", description = "Endpoints for managing favorites")
public class FavoritesController {

	private final FavoritesService favoritesService;
	private final CachedIdentityGrpcClient cachedIdentityGrpcClient;

	@Operation(summary = "List favorites", description = "Retrieves a list of all favorite entities (such as gyms or stores) that the authenticated user has saved. This allows users to quickly access their preferred locations without searching again.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Favorites returned", content = @Content(schema = @Schema(implementation = FavoritesResponses.class))),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	@GetMapping("/favorites")
	public ResponseEntity<FavoritesResponses> getFavorites() {
		String userLanguage = getUserLanguage();
		FavoritesResponses favorites = favoritesService.getFavorites(userLanguage);
		return ResponseEntity.status(HttpStatus.OK).body(favorites);
	}

	@Operation(summary = "Add favorite", description = "Adds a new favorite entity (such as a gym or store) to the authenticated user's list of favorites. The entity is identified by its type and ID. If the favorite already exists, a conflict error is returned.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Favorite created", content = @Content(schema = @Schema(implementation = FavoritesResponse.class))),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "409", description = "Already favorited", content = @Content)
	})
	@PostMapping("/favorites")
	public ResponseEntity<FavoritesResponse> addFavorite(@Valid @RequestBody FavoritesRequest request) {
		String userLanguage = getUserLanguage();
		FavoritesResponse created = favoritesService.addFavorites(request, userLanguage);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}
	
	@Operation(summary = "Remove favorite", description = "Removes a specific favorite from the authenticated user's list using the favorite's unique ID. This action permanently deletes the favorite association.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Favorite removed", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
			@ApiResponse(responseCode = "404", description = "Favorite not found", content = @Content)
	})
	@DeleteMapping("/favorites/{favoriteId}")
	public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId) {
		favoritesService.deleteFavorites(favoriteId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	private String getUserLanguage() {
		Long userId = UserContext.getCurrentUserId();
		if (userId != null) {
			try {
				az.fitnest.user.grpc.UserResponse user = cachedIdentityGrpcClient.getUserById(userId);
				String language = user.getLanguage();
				if (language != null && !language.isEmpty()) {
					return language.toUpperCase();
				}
			} catch (Exception e) {
				// Log error or ignore
			}
		}
		return "AZ";
	}
}
