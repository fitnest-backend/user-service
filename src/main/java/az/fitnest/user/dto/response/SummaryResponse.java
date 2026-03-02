package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SummaryResponse(
    UserProfileResponse user,
    CountersResponse counters,
    @JsonProperty("unread_notifications") Integer unreadNotifications
) {}
