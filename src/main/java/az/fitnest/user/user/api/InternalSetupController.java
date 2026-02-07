package az.fitnest.user.user.api;

import az.fitnest.user.user.adapter.persistence.UserProfileRepository;
import az.fitnest.user.user.domain.model.UserProfile;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Internal endpoints for service-to-service communication.
 * These endpoints are not exposed via the API Gateway.
 */
@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
@Hidden // Hide from Swagger
public class InternalSetupController {

    private final UserProfileRepository userProfileRepository;

    /**
     * Get setup status for a user.
     * Called by IAM service to check if user has completed profile setup.
     */
    @GetMapping("/setup/status")
    public ResponseEntity<Map<String, Object>> getSetupStatus(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        
        Long userIdLong = Long.parseLong(userId);
        Optional<UserProfile> profileOpt = userProfileRepository.findById(userIdLong);
        
        Map<String, Object> response = new HashMap<>();
        
        if (profileOpt.isEmpty()) {
            // No profile found - setup required
            response.put("setup_required", true);
            response.put("profile", null);
            response.put("goal", null);
            return ResponseEntity.ok(response);
        }
        
        UserProfile profile = profileOpt.get();
        
        // Check if setup is complete
        boolean setupRequired = profile.getHeightCm() == null 
                || profile.getWeightKg() == null 
                || profile.getGender() == null
                || profile.getGoalCode() == null;
        
        response.put("setup_required", setupRequired);
        
        // Build profile data
        Map<String, Object> profileData = new HashMap<>();
        profileData.put("height_cm", profile.getHeightCm());
        profileData.put("weight_kg", profile.getWeightKg());
        profileData.put("gender", profile.getGender() != null ? profile.getGender().name() : null);
        
        // Calculate age from birth date
        if (profile.getBirthDate() != null) {
            int age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
            profileData.put("age", age);
        } else {
            profileData.put("age", null);
        }
        
        response.put("profile", profileData);
        response.put("goal", profile.getGoalCode());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update user profile data.
     * Called by IAM service during setup flow.
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestBody Map<String, Object> request) {
        
        Long userIdLong = Long.parseLong(userId);
        UserProfile profile = userProfileRepository.findById(userIdLong)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userIdLong);
                    return newProfile;
                });
        
        if (request.containsKey("height_cm") && request.get("height_cm") != null) {
            profile.setHeightCm(((Number) request.get("height_cm")).intValue());
        }
        if (request.containsKey("weight_kg") && request.get("weight_kg") != null) {
            profile.setWeightKg(((Number) request.get("weight_kg")).doubleValue());
        }
        if (request.containsKey("gender") && request.get("gender") != null) {
            String genderStr = ((String) request.get("gender")).toUpperCase();
            profile.setGender(az.fitnest.user.user.domain.enums.Gender.valueOf(genderStr));
        }
        if (request.containsKey("birth_date") && request.get("birth_date") != null) {
            profile.setBirthDate(LocalDate.parse((String) request.get("birth_date")));
        }
        
        userProfileRepository.save(profile);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("height_cm", profile.getHeightCm());
        response.put("weight_kg", profile.getWeightKg());
        response.put("gender", profile.getGender() != null ? profile.getGender().name() : null);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Update user goal.
     * Called by IAM service during setup flow.
     */
    @PutMapping("/goal")
    public ResponseEntity<Map<String, Object>> updateGoal(
            @RequestHeader(value = "X-User-Id", required = true) String userId,
            @RequestBody Map<String, Object> request) {
        
        Long userIdLong = Long.parseLong(userId);
        UserProfile profile = userProfileRepository.findById(userIdLong)
                .orElseGet(() -> {
                    UserProfile newProfile = new UserProfile();
                    newProfile.setUserId(userIdLong);
                    return newProfile;
                });
        
        if (request.containsKey("goal") && request.get("goal") != null) {
            profile.setGoalCode((String) request.get("goal"));
        }
        
        userProfileRepository.save(profile);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("goal", profile.getGoalCode());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get fitness level for a user.
     */
    @GetMapping("/fitness-level")
    public ResponseEntity<Map<String, Object>> getFitnessLevel(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("level", "BEGINNER");  // Default value
        
        return ResponseEntity.ok(response);
    }

    /**
     * Complete setup for a user.
     */
    @PostMapping("/setup/complete")
    public ResponseEntity<Map<String, Object>> completeSetup(
            @RequestHeader(value = "X-User-Id", required = true) String userId) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("setup_completed", true);
        
        return ResponseEntity.ok(response);
    }
}
