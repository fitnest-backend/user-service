package az.fitnest.user.grpc;

import az.fitnest.identity.grpc.ActiveUsersKpiResponse;
import az.fitnest.identity.grpc.CustomerGrowthResponse;
import az.fitnest.identity.grpc.GetActiveUsersKpiRequest;
import az.fitnest.identity.grpc.GetCustomerGrowthRequest;
import az.fitnest.identity.grpc.IdentityAnalyticsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class IdentityAnalyticsGrpcClient {

    @GrpcClient("identity-backend")
    private IdentityAnalyticsServiceGrpc.IdentityAnalyticsServiceBlockingStub stub;

    public ActiveUsersKpiResponse getActiveUsersKpi() {
        return stub.getActiveUsersKpi(
                GetActiveUsersKpiRequest.newBuilder().build()
        );
    }

    public CustomerGrowthResponse getCustomerGrowth(String period) {
        return stub.getCustomerGrowth(
                GetCustomerGrowthRequest.newBuilder()
                        .setPeriod(period)
                        .build()
        );
    }
}
