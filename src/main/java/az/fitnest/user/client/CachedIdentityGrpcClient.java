package az.fitnest.user.client;

import az.fitnest.user.dto.response.IdentityUserResponse;
import az.fitnest.user.grpc.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CachedIdentityGrpcClient {

    private final IdentityGrpcClient identityGrpcClient;

    /**
     * Returns a cache-safe DTO. The raw protobuf {@link UserResponse} contains
     * circular references ({@code unknownFields → defaultInstanceForType}) that
     * break Jackson/Redis serialization, so we convert it here.
     */
    @Cacheable(cacheNames = "identity_users", key = "#userId", sync = true)
    public IdentityUserResponse getUserById(Long userId) {
        UserResponse raw = identityGrpcClient.getUserById(userId);
        return toDto(raw);
    }

    public IdentityUserResponse updateUserProfile(Long userId, String firstName, String lastName, String email, String mobile) {
        UserResponse raw = identityGrpcClient.updateUserProfile(userId, firstName, lastName, email, mobile);
        return toDto(raw);
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

    private IdentityUserResponse toDto(UserResponse r) {
        return IdentityUserResponse.builder()
                .userId(r.getUserId())
                .firstName(r.getFirstName())
                .lastName(r.getLastName())
                .mobile(r.getMobile())
                .email(r.getEmail())
                .profileImageUrl(r.getProfileImageUrl())
                .language(r.getLanguage())
                .setupRequired(r.getSetupRequired())
                .createdAt(r.getCreatedAt())
                .build();
    }
}