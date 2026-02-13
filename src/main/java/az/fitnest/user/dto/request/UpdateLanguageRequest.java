package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateLanguageRequest {
    @JsonProperty("language")
    private String language;
}
