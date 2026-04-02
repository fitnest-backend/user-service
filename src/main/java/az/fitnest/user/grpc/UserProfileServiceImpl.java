package az.fitnest.user.grpc;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.client.DevicePlatformGrpcClient;
import az.fitnest.user.dto.response.IdentityUserResponse;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.grpc.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.format.DateTimeFormatter;

@GrpcService
public class UserProfileServiceImpl extends UserProfileServiceGrpc.UserProfileServiceImplBase {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private CachedIdentityGrpcClient cachedIdentityClient;
    @Autowired
    private DevicePlatformGrpcClient devicePlatformGrpcClient;

    @Override
    public void getUserProfileDetails(UserProfileDetailsRequest request, StreamObserver<UserProfileDetailsResponse> responseObserver) {
        UserProfile profile = userProfileRepository.findById(request.getUserId()).orElse(null);
        IdentityUserResponse identityUser = cachedIdentityClient.getUserById(request.getUserId());
        UserProfileDetailsResponse.Builder builder = UserProfileDetailsResponse.newBuilder()
                .setUserId(request.getUserId());
        if (profile != null && identityUser != null) {
            String registrationDate = identityUser.createdAt() != null ? identityUser.createdAt() : "";
            builder.setRegistrationDate(registrationDate);
            String platform = devicePlatformGrpcClient.getUserPlatform(request.getUserId());
            builder.setPlatform(platform);
            builder.setPhoneNumber(identityUser.mobile() != null ? identityUser.mobile() : "");
            builder.setEmail(identityUser.email() != null ? identityUser.email() : "");
            builder.setBirthDate(profile.getBirthDate() != null ? profile.getBirthDate().toString() : "");
            builder.setGoal(profile.getGoalCode() != null ? profile.getGoalCode() : "");
            builder.setHeight(profile.getHeightCm() != null ? profile.getHeightCm() : 0.0);
            builder.setWeight(profile.getWeightKg() != null ? profile.getWeightKg() : 0.0);
            double bmi = 0.0;
            if (profile.getHeightCm() != null && profile.getWeightKg() != null && profile.getHeightCm() > 0) {
                double heightM = profile.getHeightCm() / 100.0;
                bmi = profile.getWeightKg() / (heightM * heightM);
            }
            builder.setBmiIndex(bmi);
            builder.setProfileImageUrl(profile.getProfileImageUrl() != null ? profile.getProfileImageUrl() : "");
            builder.setFirstName(profile.getFirstName() != null ? profile.getFirstName() : "");
            builder.setLastName(profile.getLastName() != null ? profile.getLastName() : "");
            builder.setEmail(profile.getEmail() != null ? profile.getEmail() : "");
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateProfileImage(UpdateProfileImageDetailsRequest request, StreamObserver<UserProfileDetailsStatus> responseObserver) {
        UserProfile profile = userProfileRepository.findById(request.getUserId()).orElse(null);
        boolean success = false;
        if (profile != null) {
            profile.setProfileImageUrl(request.getImageUrl());
            userProfileRepository.save(profile);
            success = true;
        }
        responseObserver.onNext(UserProfileDetailsStatus.newBuilder().setSuccess(success).build());
        responseObserver.onCompleted();
    }

    @Override
    public void getUserByEmail(UserByEmailRequest request, StreamObserver<UserProfileDetailsResponse> responseObserver) {
        UserProfile profile = userProfileRepository.findByEmail(request.getEmail()).orElse(null);
        if (profile == null) {
            responseObserver.onError(Status.NOT_FOUND.withDescription("User not found by email: " + request.getEmail()).asRuntimeException());
            return;
        }

        UserProfileDetailsResponse.Builder builder = UserProfileDetailsResponse.newBuilder()
                .setUserId(profile.getUserId())
                .setFirstName(profile.getFirstName() != null ? profile.getFirstName() : "")
                .setLastName(profile.getLastName() != null ? profile.getLastName() : "")
                .setEmail(profile.getEmail() != null ? profile.getEmail() : "")
                .setProfileImageUrl(profile.getProfileImageUrl() != null ? profile.getProfileImageUrl() : "")
                .setBirthDate(profile.getBirthDate() != null ? profile.getBirthDate().toString() : "")
                .setGoal(profile.getGoalCode() != null ? profile.getGoalCode() : "")
                .setHeight(profile.getHeightCm() != null ? profile.getHeightCm() : 0.0)
                .setWeight(profile.getWeightKg() != null ? profile.getWeightKg() : 0.0);

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }

    @Override
    public void createUserProfile(CreateProfileRequest request, StreamObserver<UserProfileDetailsStatus> responseObserver) {
        UserProfile profile = userProfileRepository.findById(request.getUserId()).orElse(new UserProfile());
        profile.setUserId(request.getUserId());
        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setEmail(request.getEmail());

        userProfileRepository.save(profile);
        responseObserver.onNext(UserProfileDetailsStatus.newBuilder().setSuccess(true).build());
        responseObserver.onCompleted();
    }
}
