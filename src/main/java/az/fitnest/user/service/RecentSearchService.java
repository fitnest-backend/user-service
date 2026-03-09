package az.fitnest.user.service;

import az.fitnest.user.dto.response.PaginatedResponse;
import az.fitnest.user.dto.response.RecentSearchDto;
import az.fitnest.user.model.entity.RecentSearch;
import az.fitnest.user.repository.RecentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecentSearchService {

    private final RecentSearchRepository recentSearchRepository;
    private static final int MAX_RECENT_SEARCHES = 10;

    @Transactional
    public void saveSearch(Long userId, String query, String type) {
        if (userId == null || query == null || query.trim().isEmpty() || type == null) {
            return;
        }

        String trimmedQuery = query.trim();

        Optional<RecentSearch> existingSearch = recentSearchRepository.findByUserIdAndTypeAndQueryIgnoreCase(userId, type, trimmedQuery);

        if (existingSearch.isPresent()) {
            RecentSearch search = existingSearch.get();
            search.setCreatedDate(LocalDateTime.now());
            recentSearchRepository.save(search);
        } else {
            RecentSearch newSearch = RecentSearch.builder()
                    .userId(userId)
                    .query(trimmedQuery)
                    .type(type)
                    .build();
            newSearch.setCreatedDate(LocalDateTime.now());
            recentSearchRepository.save(newSearch);

            long count = recentSearchRepository.countByUserIdAndType(userId, type);
            if (count > MAX_RECENT_SEARCHES) {
                List<RecentSearch> searchesToKeep = recentSearchRepository.findByUserIdAndTypeOrderByCreatedDateDesc(
                        userId, type, PageRequest.of(0, MAX_RECENT_SEARCHES));

                if (searchesToKeep.size() == MAX_RECENT_SEARCHES) {
                    List<RecentSearch> allSearches = recentSearchRepository.findByUserIdAndTypeOrderByCreatedDateDesc(
                            userId, type, PageRequest.of(0, Integer.MAX_VALUE));

                    for (int i = MAX_RECENT_SEARCHES; i < allSearches.size(); i++) {
                        recentSearchRepository.delete(allSearches.get(i));
                    }
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public PaginatedResponse<RecentSearchDto> getRecentSearches(Long userId, String type, int page, int pageSize, String sortDir) {
        if (userId == null || type == null) {
            return PaginatedResponse.<RecentSearchDto>builder()
                    .items(List.of())
                    .total(0)
                    .page(page)
                    .pageSize(pageSize)
                    .build();
        }

        int safePage = Math.max(page, 1) - 1;
        int safePageSize = Math.max(pageSize, 1);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(safePage, safePageSize, Sort.by(direction, "createdDate"));

        Page<RecentSearch> resultPage;
        if ("ALL".equalsIgnoreCase(type)) {
            resultPage = recentSearchRepository.findAllByUserId(userId, pageable);
        } else {
            resultPage = recentSearchRepository.findAllByUserIdAndType(userId, type, pageable);
        }

        List<RecentSearchDto> items = resultPage.getContent().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return PaginatedResponse.<RecentSearchDto>builder()
                .items(items)
                .total(resultPage.getTotalElements())
                .page(page)
                .pageSize(safePageSize)
                .build();
    }

    @Transactional
    public void deleteSearch(Long userId, String type, String query) {
        if (userId == null || type == null || query == null) return;
        if ("ALL".equalsIgnoreCase(type)) {
            recentSearchRepository.deleteByUserIdAndQueryIgnoreCase(userId, query.trim());
        } else {
            recentSearchRepository.deleteByUserIdAndTypeAndQueryIgnoreCase(userId, type, query.trim());
        }
    }

    @Transactional
    public void clearAllSearches(Long userId, String type) {
        if (userId == null || type == null) return;
        if ("ALL".equalsIgnoreCase(type)) {
            recentSearchRepository.deleteByUserId(userId);
        } else {
            recentSearchRepository.deleteByUserIdAndType(userId, type);
        }
    }

    private RecentSearchDto mapToDto(RecentSearch search) {
        return RecentSearchDto.builder()
                .query(search.getQuery())
                .type(search.getType())
                .createdDate(search.getCreatedDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<PackageOption> getUpgradableOptions(String currentPackage, String currentOption) {
        List<PackageOption> upgradableOptions = new ArrayList<>();

        // Fetch all options for the current package
        List<PackageOption> currentPackageOptions = packageRepository.findOptionsByPackageName(currentPackage);
        for (PackageOption option : currentPackageOptions) {
            if (option.isBetterThan(currentOption)) {
                upgradableOptions.add(option);
            }
        }

        // Fetch all options for higher-tier packages
        List<String> higherTierPackages = packageRepository.findHigherTierPackages(currentPackage);
        for (String packageName : higherTierPackages) {
            upgradableOptions.addAll(packageRepository.findOptionsByPackageName(packageName));
        }

        return upgradableOptions;
    }
}
