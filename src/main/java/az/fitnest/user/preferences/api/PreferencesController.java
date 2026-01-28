package az.fitnest.user.preferences.api;

import az.fitnest.user.preferences.api.dto.request.UpdatePreferences;
import az.fitnest.user.preferences.adapter.service.PreferencesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
@Tag(name = "Preferences", description = "Endpoints for user preferences")
public class PreferencesController {

	private final PreferencesService preferencesService;

	@PutMapping("/me/preferences")
	@Operation(summary = "Update my preferences", description = "Updates current user's preferences.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Preferences updated", content = @Content),
			@ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
	})
	public ResponseEntity<Void> updateMePreferences(@Valid @RequestBody UpdatePreferences request) {
		preferencesService.updateMePreferences(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
