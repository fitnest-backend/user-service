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
    @NotBlank(message = "Language code is required")
    @Size(max = 10, message = "Language code must be at most 10 characters")
    private String code;
}
