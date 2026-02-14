package az.fitnest.user.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "goal_references")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Fitness or health goal reference entity")
public class GoalReference {

    @Id
    @Schema(description = "Unique code identifier for the goal", example = "WEIGHT_LOSS")
    private String goalCode;

    @Column(nullable = false)
    @Schema(description = "Display title of the goal", example = "Lose Weight")
    private String title;

    @Column(nullable = false)
    @Schema(description = "Subtitle or brief description of the goal", example = "Burn fat and achieve your ideal weight")
    private String subtitle;
}
