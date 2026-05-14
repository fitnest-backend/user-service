package az.fitnest.user.dto.response;

import java.util.List;

/**
 * @author: nijataghayev
 */

public record RevenueDynamicsResponse(
        List<DataPoint> chartData,
        PackageInsight insight
) {
    public record DataPoint(
            String label,
            double amount
    ) {}

    public record PackageInsight(
            long activeSubscribers,
            double growthPercentage
    ) {}
}
