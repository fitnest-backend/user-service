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
    public PaginatedResponse<AdminUserResponse> getAllUsers(Pageable pageable, Long packageId, Integer packageDuration, String subscriptionStatus, String sort) {
        log.info("Fetching all users with filters: packageId={}, duration={}, status={}, sort={}", packageId, packageDuration, subscriptionStatus, sort);

        List<Long> filteredUserIds = null;
        String orderSort = null;

        if (sort != null) {
            if (sort.equalsIgnoreCase("finishDate_asc")) orderSort = "FINISH_DATE_ASC";
            else if (sort.equalsIgnoreCase("finishDate_desc")) orderSort = "FINISH_DATE_DESC";
        }

        // Check if we need to filter/sort by subscription data in order-backend
        if (packageId != null || packageDuration != null || subscriptionStatus != null || orderSort != null) {
            try {
                filteredUserIds = orderGrpcClient.getFilteredUserIds(packageId, packageDuration, subscriptionStatus, orderSort);
                log.info("Filtered user IDs from order-backend: {}", filteredUserIds.size());
            } catch (Exception e) {
                log.error("Failed to fetch filtered user IDs from order-backend", e);
            }
        }

        Page<UserProfile> profiles;
        if (filteredUserIds != null) {
            if (filteredUserIds.isEmpty()) {
                return new PaginatedResponse<>(List.of(), 0, pageable.getPageNumber() + 1, pageable.getPageSize());
            }
            
            // If we have a list of IDs and no order-backend sorting, we might want to sort by fullName in user-backend
            // But if we HAVE order-backend sorting (finishDate), we need to maintain that order.
            if (orderSort != null) {
                // Fetch profiles for the current page of filtered IDs manually to preserve order
                int start = (int) pageable.getOffset();
                int end = Math.min(start + pageable.getPageSize(), filteredUserIds.size());
                if (start >= filteredUserIds.size()) {
                    return new PaginatedResponse<>(List.of(), filteredUserIds.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
                }
                
                List<Long> pageIds = filteredUserIds.subList(start, end);
                List<UserProfile> profileList = userProfileRepository.findAllById(pageIds);
                // Re-sort profileList according to pageIds order
                profileList.sort(java.util.Comparator.comparingInt(p -> pageIds.indexOf(p.getUserId())));
                
                List<AdminUserResponse> items = mapToResponse(profileList);
                return new PaginatedResponse<>(items, filteredUserIds.size(), pageable.getPageNumber() + 1, pageable.getPageSize());
            } else {
                profiles = userProfileRepository.findAllByUserIdIn(filteredUserIds, pageable);
            }
        } else {
            profiles = userProfileRepository.findAll(pageable);
        }

        List<AdminUserResponse> items = mapToResponse(profiles.getContent());

        return new PaginatedResponse<>(
                items,
                profiles.getTotalElements(),
                profiles.getNumber() + 1,
                profiles.getSize()
        );
    }

    private List<AdminUserResponse> mapToResponse(List<UserProfile> profiles) {
        return profiles.stream()
                .map(profile -> {
                    String userStatus = "UNKNOWN";
                    String phoneNumber = "";
                    String createdAt = "";
                    try {
                        var identityUser = identityGrpcClient.getUserById(profile.getUserId());
                        userStatus = identityUser.getStatus();
                        phoneNumber = identityUser.getMobile();
                        createdAt = identityUser.getCreatedAt();
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
