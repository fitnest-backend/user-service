package az.fitnest.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SetupRequest {

    @Valid
    @NotNull(message = "Profile is required")
    private ProfileInfo profile;

    @Data
    public static class ProfileInfo {
        @JsonProperty("height_cm")
        private Integer heightCm;

        @JsonProperty("weight_kg")
        private Double weightKg;

        private String gender;

        @JsonProperty("birth_date")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        @Past(message = "Birth date must be in the past")
        private LocalDate birthDate;

        private String goal;
    }
}
