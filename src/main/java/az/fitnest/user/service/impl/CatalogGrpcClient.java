package az.fitnest.user.service.impl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import az.fitnest.catalog.grpc.GymServiceGrpc;
import az.fitnest.catalog.grpc.GetMainPageGymsRequest;
import az.fitnest.catalog.grpc.GetMainPageGymsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CatalogGrpcClient {
    private final az.fitnest.catalog.grpc.GymServiceGrpc.GymServiceBlockingStub gymServiceStub;

    public CatalogGrpcClient(@Value("${catalog.grpc.host:catalog-service}") String host,
                             @Value("${catalog.grpc.port:9090}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        gymServiceStub = az.fitnest.catalog.grpc.GymServiceGrpc.newBlockingStub(channel);
    }

    public GetMainPageGymsResponse getMainPageGyms() {
        GetMainPageGymsRequest request = GetMainPageGymsRequest.newBuilder().build();
        return gymServiceStub.getMainPageGyms(request);
    }
}
