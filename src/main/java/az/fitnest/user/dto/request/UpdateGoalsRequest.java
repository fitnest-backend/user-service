package az.fitnest.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateGoalsRequest(
    @NotBlank(message = "Məqsəd kodu tələb olunur")
    String goalCode
) {}
