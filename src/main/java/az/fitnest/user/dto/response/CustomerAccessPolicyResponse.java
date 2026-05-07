package az.fitnest.user.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerAccessPolicyResponse {
    private boolean canBuySubscription;
    private boolean canCreateReservationRequest;
    private boolean canUseQrEntry;
    private boolean canWriteReview;
    private boolean canSaveGym;
    private boolean canShareGym;
}