package az.fitnest.user.service.impl;

import az.fitnest.user.dto.response.CustomerAccessPolicyResponse;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.model.enums.CustomerStatus;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.service.CustomerAccessPolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerAccessPolicyServiceImpl implements CustomerAccessPolicyService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public CustomerAccessPolicyResponse getAccessPolicy(Long customerId) {
        UserProfile user = userProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isActive = user.getStatus() == CustomerStatus.ACTIVE;

        return CustomerAccessPolicyResponse.builder()
                .canBuySubscription(isActive)
                .canCreateReservationRequest(isActive)
                .canUseQrEntry(isActive)
                .canWriteReview(isActive)
                .canSaveGym(isActive)
                .canShareGym(isActive)
                .build();
    }

    @Override
    public void checkAccess(Long customerId) {
        UserProfile user = userProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == CustomerStatus.BLOCKED || user.getStatus() == CustomerStatus.DELETED) {
            throw new RuntimeException("CUSTOMER_ACTION_RESTRICTED");
        }
    }
}