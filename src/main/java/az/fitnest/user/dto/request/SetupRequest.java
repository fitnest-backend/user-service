package az.fitnest.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record SetupRequest(
    @Valid ProfileInfo profile
) {}
