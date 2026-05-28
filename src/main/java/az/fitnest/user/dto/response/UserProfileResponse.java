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
    @JsonProperty("current_subscription") String currentSubscription,
    @JsonProperty("subscription_status") String subscriptionStatus,
    @JsonProperty("notifications_enabled") Boolean notificationsEnabled,
    @JsonProperty("has_local_password") Boolean hasLocalPassword,
    @JsonProperty("is_eligible_to_have_local_password") Boolean isEligibleToHaveLocalPassword
) {}
