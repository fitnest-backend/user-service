package az.fitnest.user.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String mobile;

    private String email;

    @JsonProperty("has_account")
    private Boolean hasAccount;

    @JsonProperty("setup_required")
    private Boolean setupRequired;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    private String language;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("consent_required")
    private Boolean consentRequired;
}
