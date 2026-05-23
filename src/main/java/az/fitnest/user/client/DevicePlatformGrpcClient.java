package az.fitnest.user.client;

import az.fitnest.notifications.grpc.DevicePlatformServiceGrpc;
import az.fitnest.notifications.grpc.GetUserPlatformRequest;
import az.fitnest.notifications.grpc.GetUserPlatformResponse;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class DevicePlatformGrpcClient {

    @GrpcClient("notifications-backend")
    private DevicePlatformServiceGrpc.DevicePlatformServiceBlockingStub devicePlatformStub;

    public String getUserPlatform(Long userId) {
        GetUserPlatformRequest request = GetUserPlatformRequest.newBuilder()
                .setUserId(userId)
                .build();
        GetUserPlatformResponse response = devicePlatformStub.getUserPlatform(request);
        return response.getPlatform();
    }
}
