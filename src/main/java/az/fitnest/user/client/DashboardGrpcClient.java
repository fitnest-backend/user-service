package az.fitnest.user.client;

import az.fitnest.catalog.grpc.GymServiceGrpc;
import az.fitnest.catalog.grpc.GetMainPageGymsRequest;
import az.fitnest.order.grpc.UserSubscriptionServiceGrpc;
import az.fitnest.order.grpc.GetSubscriptionStatisticsRequest;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class DashboardGrpcClient {

    @GrpcClient("catalog-service")
    private GymServiceGrpc.GymServiceBlockingStub gymStub;

    @GrpcClient("order-service")
    private UserSubscriptionServiceGrpc.UserSubscriptionServiceBlockingStub orderStub;

    public long getPartnersCount() {
        return gymStub.getMainPageGyms(GetMainPageGymsRequest.newBuilder().build()).getItemsCount();
    }

    public long getActiveSubscriptions() {
        var response = orderStub.getSubscriptionStatistics(GetSubscriptionStatisticsRequest.newBuilder().build());
        return response.getUsersActiveOrFrozen();
    }
}
