package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
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

        @JsonProperty("birth_date")
        @Schema(description = "User's birth date in DD/MM/YYYY format", example = "15/01/1990")
        private String birthDate;

        private String goal;
    }
}
