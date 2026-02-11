package az.fitnest.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Request to update a goal reference")
public class UpdateGoalRequest {
    @NotBlank
    @Schema(description = "Display title for the goal", example = "Lose Weight")
    private String title;

    @Schema(description = "Optional subtitle or description", example = "Burn fat and achieve your ideal weight")
    private String subtitle;
}
