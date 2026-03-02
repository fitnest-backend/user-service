package az.fitnest.user.dto.request;

import az.fitnest.user.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdateBodyRequest(
    @Min(100) @Max(250) Integer heightCm,
    @DecimalMin("30.0") @DecimalMax("300.0") Double weightKg,
    Gender gender,
    @Past @Schema(type = "string", example = "15/01/1990", pattern = "^\\d{2}/\\d{2}/\\d{4}$") @JsonFormat(pattern = "dd/MM/yyyy") LocalDate birthDate
) {}
