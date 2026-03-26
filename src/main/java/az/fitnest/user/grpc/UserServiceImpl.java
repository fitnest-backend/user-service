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
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private CachedIdentityGrpcClient cachedIdentityGrpcClient;

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<UserResponse> responseObserver) {
        IdentityUserResponse identity = cachedIdentityGrpcClient.getUserById(request.getUserId());
        UserResponse.Builder builder = UserResponse.newBuilder();
        if (identity != null) {
            builder.setUserId(identity.userId() != null ? identity.userId() : 0L);
            builder.setFirstName(identity.firstName() != null ? identity.firstName() : "");
            builder.setLastName(identity.lastName() != null ? identity.lastName() : "");
            builder.setEmail(identity.email() != null ? identity.email() : "");
            builder.setMobile(identity.mobile() != null ? identity.mobile() : "");
            builder.setProfileImageUrl(identity.profileImageUrl() != null ? identity.profileImageUrl() : "");
            builder.setSetupRequired(identity.setupRequired() != null ? identity.setupRequired() : false);
            builder.setLanguage(identity.language() != null ? identity.language() : "");
            builder.setCreatedAt(identity.createdAt() != null ? identity.createdAt() : "");
        }
        responseObserver.onNext(builder.build());
        responseObserver.onCompleted();
    }
}
