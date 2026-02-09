package az.fitnest.user.profile.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetupResponse {

    @JsonProperty("setup_required")
    private Boolean setupRequired;

    @JsonProperty("profile")
    private ProfileData profile;

    @JsonProperty("goal")
    private String goal;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileData {
        @JsonProperty("height_cm")
        private Integer heightCm;

        @JsonProperty("weight_kg")
        private Double weightKg;

        @JsonProperty("gender")
        private String gender;

        @JsonProperty("age")
        private Integer age;
    }
}
