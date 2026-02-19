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
    private ProfileInfo profile;

    @Data
    public static class ProfileInfo {
        @JsonProperty("height_cm")
        @Min(100)
        @Max(250)
        private Integer heightCm;

        @JsonProperty("weight_kg")
        @DecimalMin("30.0")
        @DecimalMax("300.0")
        private Double weightKg;

        private String gender;

        @JsonProperty("birth_date")
        @Past
        @Schema(type = "string", example = "15/01/1990", pattern = "^\\d{2}/\\d{2}/\\d{4}$")
        @JsonFormat(pattern = "dd/MM/yyyy")
        private LocalDate birthDate;

        private String goal;
    }
}
