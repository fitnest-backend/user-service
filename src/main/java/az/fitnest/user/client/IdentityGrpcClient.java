package az.fitnest.user.client;

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

    private static final long DEFAULT_TIMEOUT_MS = 10000L; // 10s per-call deadline (allows for connection establishment)

    private UserServiceGrpc.UserServiceBlockingStub withDeadline() {
        return userServiceStub.withDeadlineAfter(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
    }

    @Cacheable(value = "userAuthData", key = "#userId")
    public az.fitnest.user.grpc.UserResponse getUserById(Long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        return withDeadline().getUserById(request);
    }

    public az.fitnest.user.grpc.UserResponse updateUserProfile(Long userId, String firstName, String lastName, String email) {
        az.fitnest.user.grpc.UpdateUserProfileRequest request = az.fitnest.user.grpc.UpdateUserProfileRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName != null ? firstName : "")
                .setLastName(lastName != null ? lastName : "")
                .setEmail(email != null ? email : "")
                .build();

        return withDeadline().updateUserProfile(request);
    }

    public az.fitnest.user.grpc.UserResponse updateProfileImage(Long userId, String imageUrl) {
        az.fitnest.user.grpc.UpdateProfileImageRequest request = az.fitnest.user.grpc.UpdateProfileImageRequest.newBuilder()
                .setUserId(userId)
                .setImageUrl(imageUrl != null ? imageUrl : "")
                .build();

        return withDeadline().updateProfileImage(request);
    }

    public az.fitnest.user.grpc.UserResponse updateSetupRequired(Long userId, boolean setupRequired) {
        az.fitnest.user.grpc.UpdateSetupRequiredRequest request = az.fitnest.user.grpc.UpdateSetupRequiredRequest.newBuilder()
                .setUserId(userId)
                .setSetupRequired(setupRequired)
                .build();

        return withDeadline().updateSetupRequired(request);
    }

    public az.fitnest.user.grpc.UserResponse updateLanguage(Long userId, String language) {
        az.fitnest.user.grpc.UpdateLanguageRequest request = az.fitnest.user.grpc.UpdateLanguageRequest.newBuilder()
                .setUserId(userId)
                .setLanguage(language != null ? language : "")
                .build();

        return withDeadline().updateLanguage(request);
    }

    public void deleteUser(Long userId, String reason) {
        az.fitnest.user.grpc.DeleteUserRequest request = az.fitnest.user.grpc.DeleteUserRequest.newBuilder()
                .setUserId(userId)
                .setReason(reason != null ? reason : "")
                .build();

        withDeadline().deleteUser(request);
    }
}
