package az.fitnest.user.user.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessLevelResponse {
    
    private String level;
    
    private Double bmi;
    
    @JsonProperty("bmi_category")
    private String bmiCategory;
}
