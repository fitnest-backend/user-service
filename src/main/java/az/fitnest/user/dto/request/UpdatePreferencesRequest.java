package az.fitnest.user.dto.request;

import az.fitnest.user.dto.PushNotification;
import lombok.Builder;

@Builder
public record UpdatePreferencesRequest(
    String theme,
    PushNotification notifications
) {}
