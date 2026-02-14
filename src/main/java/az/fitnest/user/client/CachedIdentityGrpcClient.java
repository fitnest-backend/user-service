package az.fitnest.user.client;

import az.fitnest.user.grpc.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachedIdentityGrpcClient {

    private final IdentityGrpcClient identityGrpcClient;

    @Cacheable(cacheNames = "identity_users", key = "#userId", sync = true)
    public UserResponse getUserById(Long userId) {
        return identityGrpcClient.getUserById(userId);
    }

    public UserResponse updateUserProfile(Long userId, String firstName, String lastName, String email) {
        return identityGrpcClient.updateUserProfile(userId, firstName, lastName, email);
    }

    public void updateProfileImage(Long userId, String imageUrl) {
        identityGrpcClient.updateProfileImage(userId, imageUrl);
    }

    public void updateLanguage(Long userId, String language) {
        identityGrpcClient.updateLanguage(userId, language);
    }

    public void updateSetupRequired(Long userId, boolean required) {
        identityGrpcClient.updateSetupRequired(userId, required);
    }

    public void deleteUser(Long userId, String reason) {
        identityGrpcClient.deleteUser(userId, reason);
    }
}