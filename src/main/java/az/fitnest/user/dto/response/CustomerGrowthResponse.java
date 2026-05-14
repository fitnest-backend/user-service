package az.fitnest.user.dto.response;

import java.util.List;

/**
 * @author: nijataghayev
 */

public record CustomerGrowthResponse(
        List<GrowthPoint> chartData,
        double overallGrowthPercentage
) {
    public record GrowthPoint(
            String periodLabel,
            long newCustomers,
            long activeCustomers
    ) {}
}
