package az.fitnest.user.model.entity;

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
public class GoalReference {

    @Id
    private String goalCode;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subtitle;
}
