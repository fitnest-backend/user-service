package az.fitnest.user.user.adapter.client;

import az.fitnest.user.user.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest;
import az.fitnest.user.user.adapter.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client for internal service-to-service communication with the IAM Service.
 * 
 * <p>This client is used by the user-service to communicate with the iam-service
 * for user management operations. All endpoints are internal APIs and are not 
 * exposed via the API Gateway.</p>
 * 
 * <h2>Internal Endpoints Used:</h2>
 * <ul>
 *   <li>{@code GET /api/v1/internal/users/{userId}} - Retrieve user by ID from IAM service</li>
 *   <li>{@code PUT /api/v1/internal/users/{userId}} - Update user profile in IAM service</li>
 *   <li>{@code PUT /api/v1/internal/users/{userId}/profile-image} - Update user's profile image URL</li>
 *   <li>{@code PUT /api/v1/internal/users/{userId}/setup-required} - Update setup required flag</li>
 *   <li>{@code DELETE /api/v1/internal/users/{userId}} - Delete user from IAM service</li>
 * </ul>
 * 
 * <p><strong>Target Service:</strong> iam-service (InternalUserController)</p>
 * 
 * @see az.fitnest.user.config.FeignConfig for authentication header forwarding configuration
 */
@FeignClient(
        name = "iam-service", 
        url = "${IAM_SERVICE_URL:http://iam-service:8080}",
        configuration = IamServiceClientConfig.class
)
public interface IamServiceClient {

    @GetMapping(value = "/api/v1/internal/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);

    @PutMapping(value = "/api/v1/internal/users/{userId}")
    UserResponse updateUserProfile(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserProfileRequest request);

    @PutMapping(value = "/api/v1/internal/users/{userId}/profile-image")
    UserResponse updateProfileImage(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateProfileImageRequest request);

    @PutMapping(value = "/api/v1/internal/users/{userId}/setup-required")
    UserResponse updateSetupRequired(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateSetupRequiredRequest request);

    @DeleteMapping(value = "/api/v1/internal/users/{userId}")
    void deleteUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "reason", required = false) String reason);
}
