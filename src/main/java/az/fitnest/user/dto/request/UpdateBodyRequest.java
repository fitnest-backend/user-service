package az.fitnest.user.dto.request;

import az.fitnest.user.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateBodyRequest {

    @NotNull
    @Min(100)
    @Max(250)
    private Integer heightCm;

    @NotNull
    @DecimalMin("30.0")
    @DecimalMax("300.0")
    private Double weightKg;

    @NotNull
    private Gender gender;

    @NotNull
    @Past
    @Schema(type = "string", example = "15/01/1990", pattern = "^\\d{2}/\\d{2}/\\d{4}$")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate birthDate;

    public Integer getHeightCm() {
        return heightCm;
    }
}
