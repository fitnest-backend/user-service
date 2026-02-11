package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyInfoResponse {

    @JsonProperty("height_cm")
    private Integer heightCm;

    @JsonProperty("weight_kg")
    private Double weightKg;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("age")
    private Integer age;
}

