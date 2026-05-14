package az.fitnest.user.service.impl;

import az.fitnest.catalog.grpc.ActivePartnersKpiResponse;
import az.fitnest.identity.grpc.ActiveUsersKpiResponse;
import az.fitnest.order.grpc.SubscriptionStatisticsResponse;
import az.fitnest.user.dto.response.CustomerGrowthResponse;
import az.fitnest.user.dto.response.KpiSummaryResponse;
import az.fitnest.user.dto.response.KpiSummaryResponse.MetricCard;
import az.fitnest.user.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: nijataghayev
 */

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final az.fitnest.user.grpc.IdentityAnalyticsGrpcClient identityClient;
    private final az.fitnest.user.grpc.CatalogAnalyticsGrpcClient catalogClient;
    private final az.fitnest.user.grpc.OrderAnalyticsGrpcClient orderClient;

    private static final String PERIOD_LABEL = "son 1 ay";

    // ── KPI Summary ───────────────────────────────────────────────────────────
    @Override
    @Cacheable(cacheNames = "dashboard-kpi", key = "'super-admin-kpi'", sync = true)
    public KpiSummaryResponse getKpiSummary() {

        // 1. identity-backend → aktiv istifadəçilər
        ActiveUsersKpiResponse userKpi = identityClient.getActiveUsersKpi();

        // 2. catalog-backend → aktiv partnyorlar
        ActivePartnersKpiResponse partnerKpi = catalogClient.getActivePartnersKpi();

        // 3. order-backend → aktiv abunəliklər
        SubscriptionStatisticsResponse subStats = orderClient.getSubscriptionStatistics();

        return new KpiSummaryResponse(
                buildMetricCard(
                        userKpi.getCurrentTotal(),
                        userKpi.getPercentageChange()
                ),
                buildMetricCard(
                        partnerKpi.getTotalActivePartners(),
                        partnerKpi.getPercentageChange()
                ),
                buildMetricCard(
                        subStats.getUsersActiveOrFrozen(),
                        0.0   // order-backend artım faizini qaytarmır, default 0
                )
        );
    }

    // ── Customer Growth ───────────────────────────────────────────────────────
    @Override
    @Cacheable(cacheNames = "dashboard-growth", key = "#period", sync = true)
    public CustomerGrowthResponse getCustomerGrowth(String period) {

        az.fitnest.identity.grpc.CustomerGrowthResponse grpcResponse =
                identityClient.getCustomerGrowth(period);

        List<CustomerGrowthResponse.GrowthPoint> points = grpcResponse.getDataPointsList()
                .stream()
                .map(p -> new CustomerGrowthResponse.GrowthPoint(
                        p.getPeriodLabel(),
                        p.getNewCustomers(),
                        p.getActiveCustomers()
                ))
                .toList();

        return new CustomerGrowthResponse(points, grpcResponse.getOverallGrowthPct());
    }

    // ── Cache Eviction — hər saat başı ───────────────────────────────────────
    @CacheEvict(
            cacheNames = {"dashboard-kpi", "dashboard-growth"},
            allEntries = true
    )
    @Scheduled(cron = "0 0 * * * *")
    public void invalidateDashboardCache() {
        // hər saat başı keş avtomatik təmizlənir
    }

    // ── Köməkçi metod ─────────────────────────────────────────────────────────
    private MetricCard buildMetricCard(long total, double percentageChange) {
        return new MetricCard(
                total,
                Math.abs(percentageChange),
                percentageChange >= 0,
                PERIOD_LABEL
        );
    }
}
