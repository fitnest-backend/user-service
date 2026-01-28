package az.fitnest.user.user.adapter.client;

import az.fitnest.user.user.adapter.client.dto.UpdateUserProfileRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateProfileImageRequest;
import az.fitnest.user.user.adapter.client.dto.UpdateSetupRequiredRequest;
import az.fitnest.user.user.adapter.client.dto.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "iam-service", url = "${iam.service.url:http://localhost:8080}")
public interface IamServiceClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);

    @PutMapping("/api/v1/internal/users/{userId}")
    UserResponse updateUserProfile(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateUserProfileRequest request);

    @PutMapping("/api/v1/internal/users/{userId}/profile-image")
    UserResponse updateProfileImage(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateProfileImageRequest request);

    @PutMapping("/api/v1/internal/users/{userId}/setup-required")
    UserResponse updateSetupRequired(
            @PathVariable("userId") Long userId,
            @RequestBody UpdateSetupRequiredRequest request);

    @DeleteMapping("/api/v1/internal/users/{userId}")
    void deleteUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "reason", required = false) String reason);
}
