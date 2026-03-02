package az.fitnest.user.client;

import az.fitnest.notifications.grpc.GetNotificationsRequest;
import az.fitnest.notifications.grpc.GetNotificationsResponse;
import az.fitnest.notifications.grpc.NotificationsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class NotificationsGrpcClient {

    @GrpcClient("notifications-service")
    private NotificationsServiceGrpc.NotificationsServiceBlockingStub notificationsStub;

    public GetNotificationsResponse getUserNotifications(Long userId, int page, int size) {
        try {
            GetNotificationsRequest request = GetNotificationsRequest.newBuilder()
                    .setUserId(userId)
                    .setPage(page)
                    .setSize(size)
                    .build();
            return notificationsStub.getNotifications(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch notifications from notifications-service", e);
        }
    }
}
