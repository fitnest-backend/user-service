package az.fitnest.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateGoalsRequest {
    
    @NotBlank(message = "Goal code is required")
    private String goalCode;
}
