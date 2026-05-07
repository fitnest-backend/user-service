package az.fitnest.user.service;

import az.fitnest.user.dto.response.CustomerAccessPolicyResponse;

public interface CustomerAccessPolicyService {
    CustomerAccessPolicyResponse getAccessPolicy(Long customerId);
    void checkAccess(Long customerId);
}