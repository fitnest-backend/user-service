package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

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
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(description = "User's birth date", example = "15/01/1990")
        private LocalDate birthDate;

        private String goal;
    }
}
