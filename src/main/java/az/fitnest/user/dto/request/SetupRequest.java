package az.fitnest.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record SetupRequest(
    @Valid ProfileInfo profile,
    @Max(250) Integer heightCm,
    @DecimalMax("300.0") Double weightKg,
    String gender,
    LocalDate birthDate,
    String goal
) {}
