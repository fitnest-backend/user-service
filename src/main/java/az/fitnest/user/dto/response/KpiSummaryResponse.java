package az.fitnest.user.dto.response;

/**
 * @author: nijataghayev
 */

public record KpiSummaryResponse(
        MetricCard activeCustomers,
        MetricCard partners,
        MetricCard activeSubscriptions,
        MetricCard qrScans
) {
    public record MetricCard(
            long totalValue,
            double percentageChange,
            boolean isPositiveTrend,
            String periodLabel
    ) {}
}
