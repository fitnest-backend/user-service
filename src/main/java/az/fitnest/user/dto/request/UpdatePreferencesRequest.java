package az.fitnest.user.dto.request;

import lombok.Builder;

@Builder
public record UpdatePreferencesRequest(
    Boolean notificationsEnabled
) {}
