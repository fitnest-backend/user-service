package az.fitnest.user.grpc;

import az.fitnest.user.client.CachedIdentityGrpcClient;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.repository.UserProfileRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;

import az.fitnest.user.dto.response.IdentityUserResponse;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private CachedIdentityGrpcClient cachedIdentityGrpcClient;

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        IdentityUserResponse identity = null;
        try {
            identity = cachedIdentityGrpcClient.getUserById(request.getUserId());
        } catch (Exception e) {
            log.warn("Failed to fetch user {} from identity-backend: {}", request.getUserId(), e.getMessage());
        }
        UserResponse.Builder builder = UserResponse.newBuilder();
        builder.setUserId(request.getUserId());
        if (identity != null) {
            builder.setFirstName(identity.firstName() != null ? identity.firstName() : "");
            builder.setLastName(identity.lastName() != null ? identity.lastName() : "");
            builder.setEmail(identity.email() != null ? identity.email() : "");
            builder.setMobile(identity.mobile() != null ? identity.mobile() : "");
            builder.setProfileImageUrl(identity.profileImageUrl() != null ? identity.profileImageUrl() : "");
            builder.setSetupRequired(identity.setupRequired() != null ? identity.setupRequired() : false);
            builder.setLanguage(identity.language() != null ? identity.language() : "");
            builder.setCreatedAt(identity.createdAt() != null ? identity.createdAt() : "");
        }

        userProfileRepository.findById(request.getUserId()).ifPresent(profile -> {
            if (profile.getGender() != null) {
                builder.setGender(profile.getGender().name());
            }
            if (profile.getFirstName() != null && !profile.getFirstName().isBlank()) {
                builder.setFirstName(profile.getFirstName());
            }
            if (profile.getLastName() != null && !profile.getLastName().isBlank()) {
                builder.setLastName(profile.getLastName());
            }
            if (profile.getProfileImageUrl() != null && !profile.getProfileImageUrl().isBlank()) {
                String imgUrl = profile.getProfileImageUrl();
                if (!imgUrl.startsWith("/") && !imgUrl.startsWith("http")) {
                    imgUrl = "/api/v1/me/profile/images/" + imgUrl;
                }
                builder.setProfileImageUrl(imgUrl);
            }
        });

        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
