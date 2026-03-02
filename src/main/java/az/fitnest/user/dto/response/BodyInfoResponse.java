package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BodyInfoResponse(
    @JsonProperty("height_cm") Integer heightCm,
    @JsonProperty("weight_kg") Double weightKg,
    @JsonProperty("gender") String gender,
    @JsonProperty("birth_date") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") @Schema(type = "string", example = "15/01/1990", pattern = "^\\d{2}/\\d{2}/\\d{4}$", description = "User's birth date") LocalDate birthDate
) {}
