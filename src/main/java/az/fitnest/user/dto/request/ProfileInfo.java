package az.fitnest.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ProfileInfo(
    @JsonProperty("height_cm")
    @Min(100)
    Integer heightCm,

    @JsonProperty("weight_kg")
    @DecimalMin("30.0")
    Double weightKg,

    @JsonProperty("gender")
    String gender,

    @JsonProperty("birth_date")
    @Past
    @Schema(type = "string", example = "15/01/1990", pattern = "^\\d{2}/\\d{2}/\\d{4}$")
    @JsonFormat(pattern = "dd/MM/yyyy")
    LocalDate birthDate,

    @JsonProperty("goal")
    String goal
) {}
