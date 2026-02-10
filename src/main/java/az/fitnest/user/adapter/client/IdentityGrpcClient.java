package az.fitnest.user.adapter.client;

import az.fitnest.user.grpc.*;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import io.grpc.Deadline;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IdentityGrpcClient {

    @GrpcClient("identity-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    private static final long DEFAULT_TIMEOUT_MS = 2000L; // 2s per-call deadline

    private UserServiceGrpc.UserServiceBlockingStub withDeadline() {
        return userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    @Cacheable(value = "userAuthData", key = "#userId")
    public UserResponse getUserById(Long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        return withDeadline().getUserById(request);
    }

    public UserResponse updateUserProfile(Long userId, String firstName, String lastName, String email) {
        UpdateUserProfileRequest request = UpdateUserProfileRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName != null ? firstName : "")
                .setLastName(lastName != null ? lastName : "")
                .setEmail(email != null ? email : "")
                .build();

        return withDeadline().updateUserProfile(request);
    }

    public UserResponse updateProfileImage(Long userId, String imageUrl) {
        UpdateProfileImageRequest request = UpdateProfileImageRequest.newBuilder()
                .setUserId(userId)
                .setImageUrl(imageUrl != null ? imageUrl : "")
                .build();

        return withDeadline().updateProfileImage(request);
    }

    public UserResponse updateSetupRequired(Long userId, boolean setupRequired) {
        UpdateSetupRequiredRequest request = UpdateSetupRequiredRequest.newBuilder()
                .setUserId(userId)
                .setSetupRequired(setupRequired)
                .build();

        return withDeadline().updateSetupRequired(request);
    }

    public UserResponse updateLanguage(Long userId, String language) {
        UpdateLanguageRequest request = UpdateLanguageRequest.newBuilder()
                .setUserId(userId)
                .setLanguage(language != null ? language : "")
                .build();

        return withDeadline().updateLanguage(request);
    }

    public void deleteUser(Long userId, String reason) {
        DeleteUserRequest request = DeleteUserRequest.newBuilder()
                .setUserId(userId)
                .setReason(reason != null ? reason : "")
                .build();

        withDeadline().deleteUser(request);
    }
}
