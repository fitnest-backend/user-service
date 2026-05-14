package az.fitnest.user.grpc;

import az.fitnest.order.grpc.GetSubscriptionStatisticsRequest;
import az.fitnest.order.grpc.SubscriptionStatisticsResponse;
import az.fitnest.order.grpc.UserSubscriptionServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class OrderAnalyticsGrpcClient {

    @GrpcClient("order-backend")
    private UserSubscriptionServiceGrpc.UserSubscriptionServiceBlockingStub stub;

    public SubscriptionStatisticsResponse getSubscriptionStatistics() {
        return stub.getSubscriptionStatistics(
                GetSubscriptionStatisticsRequest.newBuilder().build()
        );
    }
}