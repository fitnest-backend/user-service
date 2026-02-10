package az.fitnest.user.profile.adapter.client;

import az.fitnest.user.profile.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.profile.adapter.client.dto.UpdateSetupRequiredRequest;
import az.fitnest.user.profile.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.profile.adapter.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "identity-service",
    url = "${identity.service.url:http://identity-service:8080}",
    path = "/api/v1/internal/users",
    configuration = IdentityServiceClientConfig.class
)
public interface IdentityServiceClient {

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);

    @PutMapping("/{userId}")
    UserResponse updateUserProfile(@PathVariable("userId") Long userId,
                                   @RequestBody UpdateUserProfileRequest request);

    @PutMapping("/{userId}/profile-image")
    UserResponse updateProfileImage(@PathVariable("userId") Long userId,
                                    @RequestBody UpdateProfileImageRequest request);

    @PutMapping("/{userId}/setup-required")
    UserResponse updateSetupRequired(@PathVariable("userId") Long userId,
                                     @RequestBody UpdateSetupRequiredRequest request);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable("userId") Long userId,
                    @RequestParam(value = "reason", required = false) String reason);

    @PutMapping("/{userId}/language")
    void updateLanguage(@PathVariable("userId") Long userId,
                        @RequestBody az.fitnest.user.profile.adapter.client.dto.UpdateLanguageRequest request);
}
