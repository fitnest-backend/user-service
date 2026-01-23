package az.fitnest.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.request.UpdatePreferences;
import az.fitnest.userservice.service.PreferencesService;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class PreferencesController {

	private final PreferencesService preferencesService;

	@PutMapping("/me/preferences")
	public ResponseEntity<Void> updateMePreferences(@RequestBody UpdatePreferences request) {
		preferencesService.updateMePreferences(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
