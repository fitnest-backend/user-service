package az.fitnest.user.model.entity;

import az.fitnest.user.model.enums.Gender;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
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

    public UserProfile() {}

    public UserProfile(Long userId, Double heightCm, Double weightKg, Gender gender, LocalDate birthDate, String goalCode) {
        this.userId = userId;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.gender = gender;
        this.birthDate = birthDate;
        this.goalCode = goalCode;
    }

    public static UserProfileBuilder builder() {
        return new UserProfileBuilder();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getGoalCode() {
        return goalCode;
    }

    public void setGoalCode(String goalCode) {
        this.goalCode = goalCode;
    }

    public static class UserProfileBuilder {
        private Long userId;
        private Double heightCm;
        private Double weightKg;
        private Gender gender;
        private LocalDate birthDate;
        private String goalCode;

        UserProfileBuilder() {}

        public UserProfileBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public UserProfileBuilder heightCm(Double heightCm) {
            this.heightCm = heightCm;
            return this;
        }

        public UserProfileBuilder weightKg(Double weightKg) {
            this.weightKg = weightKg;
            return this;
        }

        public UserProfileBuilder gender(Gender gender) {
            this.gender = gender;
            return this;
        }

        public UserProfileBuilder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        public UserProfileBuilder goalCode(String goalCode) {
            this.goalCode = goalCode;
            return this;
        }

        public UserProfile build() {
            return new UserProfile(userId, heightCm, weightKg, gender, birthDate, goalCode);
        }
    }
}
