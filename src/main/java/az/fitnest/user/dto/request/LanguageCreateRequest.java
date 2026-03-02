package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LanguageCreateRequest(
    @NotBlank(message = "Dil kodu tələb olunur")
    @Size(max = 10, message = "Dil kodu ən çox 10 simvoldan ibarət olmalıdır")
    String code
) {}
