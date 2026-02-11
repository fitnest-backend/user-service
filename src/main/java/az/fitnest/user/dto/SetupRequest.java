package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SetupRequest {

    @Valid
    @NotNull(message = "Profile is required")
    private ProfileInfo profile;

    @Data
    public static class ProfileInfo {
        @JsonProperty("height_cm")
        private Integer heightCm;

        @JsonProperty("weight_kg")
        private Double weightKg;

        private String gender;

        @JsonProperty("age")
        @Min(value = 1, message = "Age must be at least 1")
        @Max(value = 120, message = "Age must be realistic")
        private Integer age;

        private String goal;
    }
}
