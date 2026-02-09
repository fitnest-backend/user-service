package az.fitnest.user.profile.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ActiveSubscriptionResponse {

    private String status;

    private SubscriptionInfo subscription;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionInfo {
        @JsonProperty("subscription_id")
        private String subscriptionId;

        @JsonProperty("package_name")
        private String packageName;

        @JsonProperty("start_at")
        private LocalDateTime startAt;

        @JsonProperty("end_at")
        private LocalDateTime endAt;

        @JsonProperty("total_limit")
        private Integer totalLimit;

        @JsonProperty("remaining_limit")
        private Integer remainingLimit;
    }
}
