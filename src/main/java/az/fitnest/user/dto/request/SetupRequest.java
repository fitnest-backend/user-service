package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
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
        @NotNull
        @Min(100)
        @Max(250)
        private Integer heightCm;

        @JsonProperty("weight_kg")
        @NotNull
        @DecimalMin("30.0")
        @DecimalMax("300.0")
        private Double weightKg;

        @NotNull
        private String gender;

        @JsonProperty("birth_date")
        @NotNull
        @Past
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Schema(description = "User's birth date", example = "15/01/1990")
        private LocalDate birthDate;

        @NotBlank
        private String goal;
    }
}
