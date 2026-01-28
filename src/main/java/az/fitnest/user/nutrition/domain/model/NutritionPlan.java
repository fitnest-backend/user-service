package az.fitnest.user.nutrition.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "nutrition_plans",
        indexes = {
                @Index(name = "idx_nutrition_plans_user_id", columnList = "user_id"),
                @Index(name = "idx_nutrition_plans_user_id_active", columnList = "user_id,is_active")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NutritionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive;
}
