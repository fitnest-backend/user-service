package az.fitnest.user.client;

import az.fitnest.notifications.grpc.DevicePlatformServiceGrpc;
import az.fitnest.notifications.grpc.GetUserPlatformRequest;
import az.fitnest.notifications.grpc.GetUserPlatformResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.stereotype.Service;

@Service
public class DevicePlatformGrpcClient {
    public String getUserPlatform(Long userId) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("notifications-backend", 9091)
                .usePlaintext()
                .build();
        DevicePlatformServiceGrpc.DevicePlatformServiceBlockingStub stub = DevicePlatformServiceGrpc.newBlockingStub(channel);
        GetUserPlatformRequest request = GetUserPlatformRequest.newBuilder()
                .setUserId(userId)
                .build();
        GetUserPlatformResponse response = stub.getUserPlatform(request);
        String platform = response.getPlatform();
        channel.shutdown();
        return platform;
    }
}
