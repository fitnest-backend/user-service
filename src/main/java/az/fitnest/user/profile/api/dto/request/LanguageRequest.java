package az.fitnest.user.profile.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LanguageRequest {

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(english|russian|azerbaijan)$", message = "Language must be one of: english, russian, azerbaijan")
    private String language;
}
