package az.fitnest.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "fitness_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FitnessLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @Column(name = "bmi")
    private Double bmi;
    
    @Column(name = "bmi_category")
    private String bmiCategory;
    
    @Column(name = "bmi_scale")
    private String bmiScale;
    
    @Column(name = "message")
    private String message;
}
