package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record UpdateLanguageRequest(
    @NotBlank
    @JsonProperty("language")
    String language
) {}
