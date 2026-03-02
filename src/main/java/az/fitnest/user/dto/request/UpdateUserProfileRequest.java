package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
    @JsonProperty("first_name")
    @NotBlank
    String firstName,
    @NotBlank
    @JsonProperty("last_name")
    String lastName,
    @Email
    String email,
    @NotBlank
    String mobile
) {}
