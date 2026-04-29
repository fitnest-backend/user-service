package az.fitnest.user.client;

import az.fitnest.order.grpc.GetActiveSubscriptionRequest;
import az.fitnest.order.grpc.UserSubscriptionServiceGrpc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.concurrent.TimeUnit;

@Service
public class OrderGrpcClient {

    @GrpcClient("order-backend")
    private UserSubscriptionServiceGrpc.UserSubscriptionServiceBlockingStub subscriptionServiceStub;

    @Value("${grpc.order.deadline-ms:10000}")
    private long deadlineMs;

    private UserSubscriptionServiceGrpc.UserSubscriptionServiceBlockingStub withDeadline() {
        return subscriptionServiceStub.withDeadlineAfter(deadlineMs, TimeUnit.MILLISECONDS);
    }

    public az.fitnest.order.grpc.ActiveSubscriptionResponse getActiveSubscription(Long userId) {
        GetActiveSubscriptionRequest request = GetActiveSubscriptionRequest.newBuilder()
                .setUserId(userId)
                .build();

        return withDeadline().getActiveSubscription(request);
    }

    public java.util.Map<Long, az.fitnest.order.grpc.ActiveSubscriptionResponse> getActiveSubscriptions(java.util.List<Long> userIds) {
        az.fitnest.order.grpc.GetActiveSubscriptionsRequest request = az.fitnest.order.grpc.GetActiveSubscriptionsRequest.newBuilder()
                .addAllUserIds(userIds)
                .build();

        return withDeadline().getActiveSubscriptions(request).getSubscriptionsMap();
    }

    public az.fitnest.order.grpc.SubscriptionStatisticsResponse getSubscriptionStatistics() {
        az.fitnest.order.grpc.GetSubscriptionStatisticsRequest request = az.fitnest.order.grpc.GetSubscriptionStatisticsRequest.newBuilder().build();
        return withDeadline().getSubscriptionStatistics(request);
    }

    public java.util.List<Long> getFilteredUserIds(Long packageId, Integer durationMonths, String subscriptionStatus, String sortBy) {
        var request = az.fitnest.order.grpc.GetFilteredUserIdsRequest.newBuilder();
        if (packageId != null) request.setPackageId(packageId);
        if (durationMonths != null) request.setDurationMonths(durationMonths);
        if (subscriptionStatus != null) request.setSubscriptionStatus(subscriptionStatus);
        if (sortBy != null) request.setSortBy(sortBy);

        return withDeadline().getFilteredUserIds(request.build()).getUserIdsList();
    }
}
