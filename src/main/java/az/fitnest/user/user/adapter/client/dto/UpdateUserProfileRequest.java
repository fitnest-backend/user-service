package az.fitnest.user.user.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {

    @JsonProperty("full_name")
    private String fullName;

    private String email;
}
