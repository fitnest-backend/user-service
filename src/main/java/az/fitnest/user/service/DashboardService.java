package az.fitnest.user.service;

import az.fitnest.user.dto.response.CustomerGrowthResponse;
import az.fitnest.user.dto.response.KpiSummaryResponse;

/**
 * @author: nijataghayev
 */

public interface DashboardService {
    KpiSummaryResponse getKpiSummary();
    CustomerGrowthResponse getCustomerGrowth(String period);
}