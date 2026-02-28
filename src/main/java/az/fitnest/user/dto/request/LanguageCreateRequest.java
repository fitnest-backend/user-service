package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LanguageCreateRequest {
    @NotBlank(message = "Dil kodu tələb olunur")
    @Size(max = 10, message = "Dil kodu ən çox 10 simvoldan ibarət olmalıdır")
    private String code;
}
