package az.fitnest.user.dto;

import az.fitnest.user.constants.Gender;
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
    private LocalDate birthDate;

    public Integer getHeightCm() {
        return heightCm;
    }
}
