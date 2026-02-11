package az.fitnest.user.entity;

import az.fitnest.user.constants.Gender;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity representing a user's profile information.
 * Contains physical metrics, personal details, and fitness goals.
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

    /** User's age */
    @Column(name = "age")
    private Integer age;

    /** Code representing the user's primary fitness goal */
    @Column(name = "goal")
    private String goalCode;
    
    /** URL to the user's profile image */
    @Column(name = "profile_image_url")
    private String profileImageUrl;

}
