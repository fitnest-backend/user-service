package az.fitnest.user.service.impl;

import az.fitnest.user.dto.request.BlockCustomerRequest;
import az.fitnest.user.dto.response.CustomerStatusResponse;
import az.fitnest.user.model.entity.CustomerStatusHistory;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.model.enums.CustomerStatus;
import az.fitnest.user.repository.CustomerStatusHistoryRepository;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.service.CustomerStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerStatusServiceImpl implements CustomerStatusService {

    private final UserProfileRepository userProfileRepository;
    private final CustomerStatusHistoryRepository statusHistoryRepository;

    @Override
    @Transactional
    public CustomerStatusResponse blockCustomer(Long customerId, BlockCustomerRequest request, Long adminId) {
        UserProfile user = userProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() == CustomerStatus.BLOCKED) {
            throw new RuntimeException("CUSTOMER_ALREADY_BLOCKED");
        }

        CustomerStatus previousStatus = user.getStatus();
        user.setStatus(CustomerStatus.BLOCKED);
        userProfileRepository.save(user);

        statusHistoryRepository.save(CustomerStatusHistory.builder()
                .userId(customerId)
                .previousStatus(previousStatus)
                .newStatus(CustomerStatus.BLOCKED)
                .reason(request.getReason())
                .note(request.getNote())
                .adminId(adminId)
                .build());

        return CustomerStatusResponse.builder()
                .customerId(customerId)
                .status(CustomerStatus.BLOCKED)
                .blockedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public void unblockCustomer(Long customerId, Long adminId) {
        UserProfile user = userProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getStatus() != CustomerStatus.BLOCKED) {
            throw new RuntimeException("CUSTOMER_NOT_BLOCKED");
        }

        CustomerStatus previousStatus = user.getStatus();
        user.setStatus(CustomerStatus.ACTIVE);
        userProfileRepository.save(user);

        statusHistoryRepository.save(CustomerStatusHistory.builder()
                .userId(customerId)
                .previousStatus(previousStatus)
                .newStatus(CustomerStatus.ACTIVE)
                .adminId(adminId)
                .build());
    }

    @Override
    public CustomerStatusResponse getCustomerStatus(Long customerId) {
        UserProfile user = userProfileRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return CustomerStatusResponse.builder()
                .customerId(customerId)
                .status(user.getStatus())
                .build();
    }

    @Override
    public List<CustomerStatusHistory> getStatusHistory(Long customerId) {
        return statusHistoryRepository.findByUserIdOrderByCreatedAtDesc(customerId);
    }
}