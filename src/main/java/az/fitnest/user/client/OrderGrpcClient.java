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
}
