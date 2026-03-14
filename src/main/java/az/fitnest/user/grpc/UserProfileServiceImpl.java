package az.fitnest.user.grpc;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.client.DevicePlatformGrpcClient;
import az.fitnest.user.dto.response.IdentityUserResponse;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.grpc.GetUserProfileDetailsRequest;
import az.fitnest.user.grpc.UserProfileDetailsResponse;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service("grpcUserProfileServiceImpl")
public class UserProfileServiceImpl extends UserProfileServiceGrpc.UserProfileServiceImplBase {
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private CachedIdentityGrpcClient cachedIdentityClient;
    @Autowired
    private DevicePlatformGrpcClient devicePlatformGrpcClient;

    @Override
    public void getUserProfileDetails(GetUserProfileDetailsRequest request, StreamObserver<UserProfileDetailsResponse> responseObserver) {
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
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
