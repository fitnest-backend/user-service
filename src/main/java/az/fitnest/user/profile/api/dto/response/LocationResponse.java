package az.fitnest.user.profile.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LocationResponse {

    private Double lat;
    private Double lng;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
