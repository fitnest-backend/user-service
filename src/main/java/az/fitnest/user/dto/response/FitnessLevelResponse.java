package az.fitnest.user.dto.response;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FitnessLevelResponse {
    private Double bmi;
    private String goal;

}
