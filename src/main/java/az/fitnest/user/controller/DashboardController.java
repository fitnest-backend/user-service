package az.fitnest.user.controller;

import az.fitnest.user.dto.response.CustomerGrowthResponse;
import az.fitnest.user.dto.response.KpiSummaryResponse;
import az.fitnest.user.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: nijataghayev
 */

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_SUPER_ADMIN', 'ROLE_OWNER')")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/kpi-summary")
    public ResponseEntity<KpiSummaryResponse> getKpiSummary() {
        return ResponseEntity.ok(dashboardService.getKpiSummary());
    }

    @GetMapping("/customer-growth")
    public ResponseEntity<CustomerGrowthResponse> getCustomerGrowth(
            @RequestParam(defaultValue = "MONTHLY") String period
    ) {
        return ResponseEntity.ok(dashboardService.getCustomerGrowth(period));
    }
}