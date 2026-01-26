package az.fitnest.user.user.adapter.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateSetupRequiredRequest {

    @JsonProperty("setup_required")
    private Boolean setupRequired;
}
