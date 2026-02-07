package az.fitnest.user.user.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for the IAM service's internal user update endpoint.
 * 
 * <p>This must match the IAM service's UpdateUserProfileRequest which expects
 * separate first_name and last_name fields (both required).</p>
 */
@Data
public class UpdateUserProfileRequest {

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
}
