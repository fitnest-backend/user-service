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
    public PaginatedResponse<AdminUserResponse> getAllUsers(Pageable pageable, Long packageId, Integer packageDuration, String subscriptionStatus, String sort, String search) {
        log.info("Fetching all users with filters: packageId={}, duration={}, status={}, sort={}, search={}", packageId, packageDuration, subscriptionStatus, sort, search);

        List<Long> filteredUserIds = null;
        String orderSort = null;
        org.springframework.data.domain.Sort localSort = org.springframework.data.domain.Sort.unsorted();

        if (sort != null) {
            if (sort.equalsIgnoreCase("finishDate_asc")) orderSort = "FINISH_DATE_ASC";
            else if (sort.equalsIgnoreCase("finishDate_desc")) orderSort = "FINISH_DATE_DESC";
            else if (sort.equalsIgnoreCase("newest")) localSort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "userId");
            else if (sort.equalsIgnoreCase("name_asc")) localSort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "firstName");
            else if (sort.equalsIgnoreCase("name_desc")) localSort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "firstName");
            else if (sort.equalsIgnoreCase("registrationDate_desc")) localSort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "userId");
            else if (sort.equalsIgnoreCase("registrationDate_asc")) localSort = org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "userId");
        }

        // Apply local sort to pageable if a local sort option was selected
        if (localSort.isSorted()) {
            pageable = org.springframework.data.domain.PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), localSort);
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

        // Handle search query
        boolean hasSearch = search != null && !search.trim().isEmpty();

        Page<UserProfile> profiles;
        if (filteredUserIds != null) {
            if (filteredUserIds.isEmpty()) {
                return new PaginatedResponse<>(List.of(), 0, pageable.getPageNumber() + 1, pageable.getPageSize());
            }
            
            if (hasSearch) {
                // Apply search within the filtered user IDs
                Long searchUserId = parseUserId(search);
                List<Long> mobileUserIds = searchMobileUserIds(search);
                
                // Intersect: keep only IDs that match both subscription filter AND search
                List<Long> searchMatchIds = getSearchMatchedUserIds(search, searchUserId, mobileUserIds);
                filteredUserIds = filteredUserIds.stream()
                        .filter(searchMatchIds::contains)
                        .collect(Collectors.toList());
                
                if (filteredUserIds.isEmpty()) {
                    return new PaginatedResponse<>(List.of(), 0, pageable.getPageNumber() + 1, pageable.getPageSize());
                }
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
        } else if (hasSearch) {
            // Search without subscription filters
            Long searchUserId = parseUserId(search);
            List<Long> mobileUserIds = searchMobileUserIds(search);
            
            if (mobileUserIds != null && !mobileUserIds.isEmpty()) {
                profiles = userProfileRepository.searchByQueryOrUserIds(search.trim(), searchUserId, mobileUserIds, pageable);
            } else {
                profiles = userProfileRepository.searchByQuery(search.trim(), searchUserId, pageable);
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

    private Long parseUserId(String search) {
        try {
            return Long.parseLong(search.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private List<Long> searchMobileUserIds(String search) {
        try {
            return identityGrpcClient.searchUserIdsByMobile(search.trim());
        } catch (Exception e) {
            log.warn("Failed to search users by mobile via gRPC: {}", e.getMessage());
            return List.of();
        }
    }

    private List<Long> getSearchMatchedUserIds(String search, Long searchUserId, List<Long> mobileUserIds) {
        // Get IDs matching local DB search (id, name, email)
        Page<UserProfile> localMatches = userProfileRepository.searchByQuery(
                search.trim(), searchUserId, Pageable.unpaged());
        
        java.util.Set<Long> matchedIds = new java.util.LinkedHashSet<>();
        localMatches.getContent().forEach(p -> matchedIds.add(p.getUserId()));
        if (mobileUserIds != null) {
            matchedIds.addAll(mobileUserIds);
        }
        return new java.util.ArrayList<>(matchedIds);
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
