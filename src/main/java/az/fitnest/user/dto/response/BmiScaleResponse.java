package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BmiScaleResponse {
    private @JsonProperty("underweight_max") Double underweightMax;
    private @JsonProperty("normal_max") Double normalMax;
    private @JsonProperty("overweight_max") Double overweightMax;

}
