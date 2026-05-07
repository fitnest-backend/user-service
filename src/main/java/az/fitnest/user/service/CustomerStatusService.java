package az.fitnest.user.service;

import az.fitnest.user.dto.request.BlockCustomerRequest;
import az.fitnest.user.dto.response.CustomerStatusResponse;
import az.fitnest.user.model.entity.CustomerStatusHistory;

import java.util.List;

public interface CustomerStatusService {
    CustomerStatusResponse blockCustomer(Long customerId, BlockCustomerRequest request, Long adminId);
    void unblockCustomer(Long customerId, Long adminId);
    CustomerStatusResponse getCustomerStatus(Long customerId);
    List<CustomerStatusHistory> getStatusHistory(Long customerId);
}