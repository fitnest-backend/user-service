package az.fitnest.user.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateLanguageRequest {
    @JsonProperty("language")
    private String language;
}
