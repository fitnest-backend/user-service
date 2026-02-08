@FeignClient(
    name = "iam-service",
    url = "${iam.service.url:http://iam-service:8080}",
    path = "/api/v1/internal/users",
    configuration = IamServiceClientConfig.class
)
public interface IamServiceClient {

    @GetMapping("/{userId}")
    UserResponse getUserById(@PathVariable Long userId);

    @PutMapping("/{userId}")
    UserResponse updateUserProfile(@PathVariable Long userId,
                                   @RequestBody UpdateUserProfileRequest request);

    @PutMapping("/{userId}/profile-image")
    UserResponse updateProfileImage(@PathVariable Long userId,
                                    @RequestBody UpdateProfileImageRequest request);

    @PutMapping("/{userId}/setup-required")
    UserResponse updateSetupRequired(@PathVariable Long userId,
                                     @RequestBody UpdateSetupRequiredRequest request);

    @DeleteMapping("/{userId}")
    void deleteUser(@PathVariable Long userId,
                    @RequestParam(required = false) String reason);
}