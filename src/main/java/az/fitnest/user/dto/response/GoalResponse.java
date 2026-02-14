package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {

    @JsonProperty("code")
    private String goalCode;

    private String title;

    private String subtitle;

    private String imageUrl;
}
