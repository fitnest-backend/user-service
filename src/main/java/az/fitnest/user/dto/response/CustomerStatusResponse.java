package az.fitnest.user.dto.response;

import az.fitnest.user.model.enums.CustomerStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CustomerStatusResponse {
    private Long customerId;
    private CustomerStatus status;
    private LocalDateTime blockedAt;
}