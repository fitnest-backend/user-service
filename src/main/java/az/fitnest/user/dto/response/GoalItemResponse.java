package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public record GoalItemResponse(
    String code,
    String title,
    String subtitle,
    String imageUrl
) {}
