package az.fitnest.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "goal_references")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalReference {
    
    @Id
    @Column(name = "code", nullable = false)
    private String code;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "subtitle")
    private String subtitle;
}
