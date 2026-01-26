package az.fitnest.user.goal.api.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalItemResponse {
    private String code;
    private String title;
    private String subtitle;
}
