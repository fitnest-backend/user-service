package az.fitnest.user.profile.adapter.client;

import az.fitnest.user.profile.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.profile.adapter.client.dto.UpdateSetupRequiredRequest;
import az.fitnest.user.profile.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.profile.adapter.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
    name = "iam-service",
    url = "${iam.service.url:http://iam-service:8080}",
    path = "/api/v1/internal/users",
    configuration = IamServiceClientConfig.class
)
public interface IamServiceClient {

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
