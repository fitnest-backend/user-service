package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record GoalResponse(
    @JsonProperty("code") String goalCode,
    String title,
    String subtitle,
    String imageUrl
) {}
