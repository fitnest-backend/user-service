package az.fitnest.userservice.user.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UserResponse {

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("full_name")
    private String fullName;

    private String mobile;

    private String email;

    @JsonProperty("has_account")
    private Boolean hasAccount;

    @JsonProperty("setup_required")
    private Boolean setupRequired;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    private String language;
}
