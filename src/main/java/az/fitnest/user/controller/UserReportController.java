package az.fitnest.user.controller;

import az.fitnest.user.grpc.IdentityAnalyticsGrpcClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/v1/admin/reports")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Reports Admin", description = "İstifadəçi üzrə hesabat ucluqları")
@SecurityRequirement(name = "bearerAuth")
public class UserReportController {

    private final IdentityAnalyticsGrpcClient identityClient;

    public record GrowthPointResponse(
        String periodLabel,
        long newCustomers,
        long activeCustomers
    ) {}

    public record UserReportResponse(
        long activeUsers,
        long newRegistrations,
        List<GrowthPointResponse> growthTrend
    ) {}

    @Operation(summary = "İstifadəçi hesabat məlumatlarını gətir")
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserReportResponse> getUserReport(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        long activeUsers = 0;
        try {
            var kpi = identityClient.getActiveUsersKpi();
            activeUsers = kpi.getCurrentTotal();
        } catch (Exception e) {
            log.warn("Failed to fetch active users KPI: {}", e.getMessage());
        }

        List<GrowthPointResponse> growthTrend = new ArrayList<>();
        long newRegistrations = 0;
        try {
            var growth = identityClient.getCustomerGrowth("MONTHLY");
            if (growth != null && growth.getDataPointsList() != null) {
                for (var point : growth.getDataPointsList()) {
                    growthTrend.add(new GrowthPointResponse(
                            point.getPeriodLabel(),
                            point.getNewCustomers(),
                            point.getActiveCustomers()
                    ));
                    newRegistrations += point.getNewCustomers();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to fetch customer growth statistics: {}", e.getMessage());
        }

        return ResponseEntity.ok(new UserReportResponse(activeUsers, newRegistrations, growthTrend));
    }
}
