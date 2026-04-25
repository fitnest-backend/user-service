package az.fitnest.user.service;

import az.fitnest.user.dto.PaginatedResponse;
import az.fitnest.user.dto.response.AdminUserResponse;
import az.fitnest.user.dto.response.UserStatisticsResponse;
import org.springframework.data.domain.Pageable;

public interface AdminUserService {
    PaginatedResponse<AdminUserResponse> getAllUsers(Pageable pageable);
    UserStatisticsResponse getUserStatistics();
}
