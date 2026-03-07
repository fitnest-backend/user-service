package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.io.Serializable;

@Builder
public record IdentityUserResponse(
    @JsonProperty("user_id") Long userId,
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    String mobile,
    String email,
    String language,
    @JsonProperty("profile_image_url") String profileImageUrl,
    @JsonProperty("setup_required") Boolean setupRequired,
    @JsonProperty("created_at") String createdAt
) implements Serializable {}
