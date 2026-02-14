package az.fitnest.user.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        @Schema(type = "string", description = "Subscription start timestamp", example = "15/01/2023 10:30:00", pattern = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$")
        private LocalDateTime startAt;

        @JsonProperty("end_at")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
        @Schema(type = "string", description = "Subscription end timestamp", example = "15/07/2023 10:30:00", pattern = "^\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}$")
        private LocalDateTime endAt;

        @JsonProperty("total_limit")
        private Integer totalLimit;

        @JsonProperty("remaining_limit")
        private Integer remainingLimit;
    }
}
