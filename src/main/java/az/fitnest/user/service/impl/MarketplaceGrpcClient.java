package az.fitnest.user.service.impl;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import marketplace.GymServiceGrpc;
import marketplace.GymServiceOuterClass.GetMainPageGymsRequest;
import marketplace.GymServiceOuterClass.GetMainPageGymsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MarketplaceGrpcClient {
    private final GymServiceGrpc.GymServiceBlockingStub gymServiceStub;

    public MarketplaceGrpcClient(@Value("${marketplace.grpc.host:marketplace-service}") String host,
                                 @Value("${marketplace.grpc.port:9090}") int port) {
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
