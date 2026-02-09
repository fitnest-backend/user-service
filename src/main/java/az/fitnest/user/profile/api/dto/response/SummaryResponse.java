package az.fitnest.user.profile.api.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SummaryResponse {

    private UserProfileResponse user;

    private CountersResponse counters;

    @JsonProperty("unread_notifications")
    private Integer unreadNotifications;
}
