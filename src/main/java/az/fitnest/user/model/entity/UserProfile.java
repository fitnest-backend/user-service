package az.fitnest.user.model.entity;

import az.fitnest.user.model.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profiles", indexes = {
    @Index(name = "idx_user_profile_first_name", columnList = "first_name"),
    @Index(name = "idx_user_profile_last_name", columnList = "last_name"),
    @Index(name = "idx_user_profile_email", columnList = "email")
})
public class UserProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "height_cm")
    private Double heightCm;

    @Column(name = "weight_kg")
    private Double weightKg;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "goal_code")
    private String goalCode;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;
}
