package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiScaleResponse {
    @JsonProperty("underweight_max")
    private Double underweightMax;

    @JsonProperty("normal_max")
    private Double normalMax;

    @JsonProperty("overweight_max")
    private Double overweightMax;
}
