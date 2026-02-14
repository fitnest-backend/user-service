package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateLanguageRequest {
    @JsonProperty("language")
    @NotBlank
    private String language;
}
