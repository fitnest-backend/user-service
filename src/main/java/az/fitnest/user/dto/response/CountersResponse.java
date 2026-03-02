package az.fitnest.user.dto.response;

import lombok.Builder;

@Builder
public record CountersResponse(
    Long favorite_gyms,
    Long favorite_stores
) {}
