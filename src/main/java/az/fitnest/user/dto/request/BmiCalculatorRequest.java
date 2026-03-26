package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record BmiCalculatorRequest(
    @NotNull Double height,
    @NotNull Double weight,
    @NotNull LocalDate birthDate,
    @NotNull String gender
) {}
