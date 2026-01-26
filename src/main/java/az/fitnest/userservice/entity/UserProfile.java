package az.fitnest.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

import az.fitnest.userservice.enums.Gender;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {
    
    @Id
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "height_cm")
    private Integer heightCm;
    
    @Column(name = "weight_kg")
    private Double weightKg;
    
    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "goal")
    private String goalCode;
}
