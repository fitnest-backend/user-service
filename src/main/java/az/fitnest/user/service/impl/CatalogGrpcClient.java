package az.fitnest.user.service.impl;

import az.fitnest.catalog.grpc.GymServiceGrpc;
import az.fitnest.catalog.grpc.GetMainPageGymsRequest;
import az.fitnest.catalog.grpc.GetMainPageGymsResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class CatalogGrpcClient {

    @GrpcClient("catalog-backend")
    private GymServiceGrpc.GymServiceBlockingStub gymServiceStub;

    public GetMainPageGymsResponse getMainPageGyms() {
        GetMainPageGymsRequest request = GetMainPageGymsRequest.newBuilder().build();
        return gymServiceStub.getMainPageGyms(request);
    }

    public az.fitnest.catalog.grpc.GetGymAdminsByUsersResponse getGymAdminsByUsers(java.util.List<Long> userIds) {
        az.fitnest.catalog.grpc.GetGymAdminsByUsersRequest request = az.fitnest.catalog.grpc.GetGymAdminsByUsersRequest.newBuilder()
                .addAllUserIds(userIds)
                .build();
        return gymServiceStub.getGymAdminsByUsers(request);
    }
}
