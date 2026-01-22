package az.fitnest.userservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import az.fitnest.userservice.request.UpdatePreferences;
import az.fitnest.userservice.service_inter.PreferencesInter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1")
@RequiredArgsConstructor
public class PreferencesController {

	private final PreferencesInter preferencesInter;

	@PutMapping("/me/preferences")
	public ResponseEntity<Void> updateMePreferences(@RequestBody UpdatePreferences request) {
		preferencesInter.updateMePreferences(request);
		return ResponseEntity.status(HttpStatus.OK).build();
	}

}
