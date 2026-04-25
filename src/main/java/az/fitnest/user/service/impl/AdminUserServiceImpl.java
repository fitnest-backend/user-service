package az.fitnest.user.service.impl;

import az.fitnest.user.client.IdentityGrpcClient;
import az.fitnest.user.client.OrderGrpcClient;
import az.fitnest.user.dto.PaginatedResponse;
import az.fitnest.user.dto.response.AdminUserResponse;
import az.fitnest.user.dto.response.UserStatisticsResponse;
import az.fitnest.user.model.entity.UserProfile;
import az.fitnest.user.repository.UserProfileRepository;
import az.fitnest.user.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserProfileRepository userProfileRepository;
    private final IdentityGrpcClient identityGrpcClient;
    private final OrderGrpcClient orderGrpcClient;

    @Override
    public PaginatedResponse<AdminUserResponse> getAllUsers(Pageable pageable) {
        Page<UserProfile> profiles = userProfileRepository.findAll(pageable);
        
        List<AdminUserResponse> items = profiles.getContent().stream()
                .map(profile -> {
                    String userStatus = "UNKNOWN";
                    String phoneNumber = "";
                    try {
                        var identityUser = identityGrpcClient.getUserById(profile.getUserId());
                        userStatus = identityUser.getStatus();
                        phoneNumber = identityUser.getMobile();
                    } catch (Exception e) {
                        log.warn("Failed to fetch identity info for user {}: {}", profile.getUserId(), e.getMessage());
                    }

                    String subscriptionStatus = "";
                    try {
                        var sub = orderGrpcClient.getActiveSubscription(profile.getUserId());
                        subscriptionStatus = sub.getSubscriptionStatus();
                    } catch (Exception e) {
                        log.warn("Failed to fetch subscription info for user {}: {}", profile.getUserId(), e.getMessage());
                    }

                    String fullName = (profile.getFirstName() != null ? profile.getFirstName() : "") + 
                                     (profile.getLastName() != null ? " " + profile.getLastName() : "");

                    return new AdminUserResponse(
                            profile.getUserId(),
                            fullName.trim(),
                            phoneNumber,
                            profile.getEmail(),
                            userStatus,
                            subscriptionStatus
                    );
                })
                .collect(Collectors.toList());

        return new PaginatedResponse<>(
                items,
                profiles.getTotalElements(),
                profiles.getNumber() + 1,
                profiles.getSize()
        );
    }

    @Override
    public UserStatisticsResponse getUserStatistics() {
        long totalUsers = userProfileRepository.count();
        
        long usersWithLast7Days = 0;
        long finishedSubscriptions = 0;
        long activeOrFrozenSubscriptions = 0;

        try {
            var stats = orderGrpcClient.getSubscriptionStatistics();
            usersWithLast7Days = stats.getUsersLast7Days();
            finishedSubscriptions = stats.getUsersFinished();
            activeOrFrozenSubscriptions = stats.getUsersActiveOrFrozen();
        } catch (Exception e) {
            log.error("Failed to fetch subscription statistics from order-backend", e);
        }

        return UserStatisticsResponse.builder()
                .totalUsers(totalUsers)
                .usersWithLast7Days(usersWithLast7Days)
                .finishedSubscriptions(finishedSubscriptions)
                .activeOrFrozenSubscriptions(activeOrFrozenSubscriptions)
                .build();
    }
}
