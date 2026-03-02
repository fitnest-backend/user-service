package az.fitnest.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update a goal reference")
public record UpdateGoalRequest(
    @NotBlank
    @Schema(description = "Display title for the goal", example = "Lose Weight")
    String title,

    @Schema(description = "Optional subtitle or description", example = "Burn fat and achieve your ideal weight")
    String subtitle
) {}
