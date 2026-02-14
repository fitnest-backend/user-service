package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BodyInfoResponse {

    @JsonProperty("height_cm")
    private Integer heightCm;

    @JsonProperty("weight_kg")
    private Double weightKg;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    @Schema(description = "User's birth date", example = "15/01/1990")
    private LocalDate birthDate;
}
