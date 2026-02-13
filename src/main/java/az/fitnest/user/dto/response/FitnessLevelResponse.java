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
public class FitnessLevelResponse {

    private String level;

    private Double bmi;

    @JsonProperty("bmi_category")
    private String bmiCategory;

    @JsonProperty("bmi_scale")
    private BmiScale bmiScale;

    private String goal;

    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BmiScale {
        @JsonProperty("underweight_max")
        private Double underweightMax;

        @JsonProperty("normal_max")
        private Double normalMax;

        @JsonProperty("overweight_max")
        private Double overweightMax;
    }
}
