package az.fitnest.user.grpc;

import az.fitnest.catalog.grpc.ActivePartnersKpiResponse;
import az.fitnest.catalog.grpc.GetActivePartnersKpiRequest;
import az.fitnest.catalog.grpc.GymServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class CatalogAnalyticsGrpcClient {

    @GrpcClient("catalog-backend")
    private GymServiceGrpc.GymServiceBlockingStub stub;

    public ActivePartnersKpiResponse getActivePartnersKpi() {
        return stub.getActivePartnersKpi(
                GetActivePartnersKpiRequest.newBuilder().build()
        );
    }
}
