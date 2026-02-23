package az.fitnest.user.service.impl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import catalog.GymServiceGrpc;
import catalog.GymServiceOuterClass.GetMainPageGymsRequest;
import catalog.GymServiceOuterClass.GetMainPageGymsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CatalogGrpcClient {
    private final GymServiceGrpc.GymServiceBlockingStub gymServiceStub;

    public CatalogGrpcClient(@Value("${catalog.grpc.host:catalog-service}") String host,
                                 @Value("${catalog.grpc.port:9090}") int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        gymServiceStub = GymServiceGrpc.newBlockingStub(channel);
    }

    public GetMainPageGymsResponse getMainPageGyms() {
        GetMainPageGymsRequest request = GetMainPageGymsRequest.newBuilder().build();
        return gymServiceStub.getMainPageGyms(request);
    }
}
