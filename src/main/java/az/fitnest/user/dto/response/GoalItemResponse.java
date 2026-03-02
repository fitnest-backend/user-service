package az.fitnest.user.dto.response;

import lombok.Builder;

@Builder
public record GoalItemResponse(
    String code,
    String title,
    String subtitle,
    String imageUrl
) {}
