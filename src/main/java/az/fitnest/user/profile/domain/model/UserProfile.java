package az.fitnest.user.profile.domain.model;

import az.fitnest.user.profile.domain.enums.Gender;
import az.fitnest.user.shared.persistence.BaseAuditableEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entity representing a user's profile information.
 * Contains physical metrics, personal details, and fitness goals.
 *
 * <p>This entity stores:
 * <ul>
 *   <li>Physical measurements (height, weight)</li>
 *   <li>Personal information (gender, birth date)</li>
 *   <li>Fitness goals and preferences</li>
 *   <li>Profile image URL</li>
 * </ul>
 *
 * <p>The user_id serves as both the primary key and foreign key
 * linking to the User entity in the identity service.
 */
@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

    /** User ID - also serves as the primary key */
    @Id
    @Column(name = "user_id")
    private Long userId;

    /** User's height in centimeters */
    @Column(name = "height_cm")
    private Integer heightCm;

    /** User's weight in kilograms */
    @Column(name = "weight_kg")
    private Double weightKg;

    /** User's gender */
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    /** User's date of birth */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    /** Code representing the user's primary fitness goal */
    @Column(name = "goal")
    private String goalCode;
    
    /** URL to the user's profile image */
    @Column(name = "profile_image_url")
    private String profileImageUrl;
}
