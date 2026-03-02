package az.fitnest.user.client;

import az.fitnest.user.grpc.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.concurrent.TimeUnit;

@Service
public class IdentityGrpcClient {

    @GrpcClient("identity-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Value("${grpc.identity.deadline-ms:10000}")
    private long deadlineMs;

    private UserServiceGrpc.UserServiceBlockingStub withDeadline() {
        return userServiceStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
    }

    public az.fitnest.user.grpc.UserResponse getUserById(Long userId) {
        GetUserByIdRequest request = GetUserByIdRequest.newBuilder()
                .setUserId(userId)
                .build();

        return withDeadline().getUserById(request);
    }

    public az.fitnest.user.grpc.UserResponse updateUserProfile(Long userId, String firstName, String lastName) {
        az.fitnest.user.grpc.UpdateUserProfileRequest request = az.fitnest.user.grpc.UpdateUserProfileRequest.newBuilder()
                .setUserId(userId)
                .setFirstName(firstName != null ? firstName : "")
                .setLastName(lastName != null ? lastName : "")
                .build();

        return withDeadline().updateUserProfile(request);
    }

    public void requestEmailChange(Long userId, String newEmail) {
        RequestEmailChangeRequest request = RequestEmailChangeRequest.newBuilder()
                .setUserId(userId)
                .setNewEmail(newEmail)
                .build();

        withDeadline().requestEmailChange(request);
    }

    public az.fitnest.user.grpc.UserResponse confirmEmailChange(Long userId, String otpCode) {
        ConfirmEmailChangeRequest request = ConfirmEmailChangeRequest.newBuilder()
                .setUserId(userId)
                .setOtpCode(otpCode)
                .build();

        return withDeadline().confirmEmailChange(request);
    }

    public void requestMobileChange(Long userId, String newMobile) {
        RequestMobileChangeRequest request = RequestMobileChangeRequest.newBuilder()
                .setUserId(userId)
                .setNewMobile(newMobile)
                .build();

        withDeadline().requestMobileChange(request);
    }

    public az.fitnest.user.grpc.UserResponse confirmMobileChange(Long userId, String otpCode) {
        ConfirmMobileChangeRequest request = ConfirmMobileChangeRequest.newBuilder()
                .setUserId(userId)
                .setOtpCode(otpCode)
                .build();

        return withDeadline().confirmMobileChange(request);
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
