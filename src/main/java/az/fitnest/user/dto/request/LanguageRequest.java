package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LanguageRequest {

    @NotBlank(message = "Dil tələb olunur")
    @Pattern(regexp = "^(english|russian|azerbaijan)$", message = "Dil bunlardan biri olmalıdır: ingilis, rus, azərbaycan")
    private String language;
}
