package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserProfileResponse(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    String mobile,
    String email,
    @JsonProperty("profile_image_url") String profileImageUrl,
    @JsonProperty("profileImage") String profileImage,
    @JsonProperty("current_subscription") String currentSubscription
) {}
