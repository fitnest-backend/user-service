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

    @Column
    @Schema(description = "URL of the goal image", example = "https://example.com/goal-image.jpg")
    private String imageUrl;

    @Column
    @Schema(description = "Azerbaijani title of the goal", example = "Çəki İdarəsi")
    private String title;

    @Column
    @Schema(description = "Azerbaijani subtitle/description of the goal", example = "Fiziki fəaliyyət və kardio ilə artıq çəkini azaltmaq üçün məşqlər.")
    private String subtitle;
}
