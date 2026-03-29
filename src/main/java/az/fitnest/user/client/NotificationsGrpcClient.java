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

    public void setUserNotificationPreference(Long userId, Boolean notificationsEnabled) {
        try {
            az.fitnest.notifications.grpc.SetUserNotificationPreferenceRequest request =
                    az.fitnest.notifications.grpc.SetUserNotificationPreferenceRequest.newBuilder()
                            .setUserId(userId)
                            .setNotificationsEnabled(notificationsEnabled)
                            .build();
            az.fitnest.notifications.grpc.SetUserNotificationPreferenceResponse response =
                    notificationsStub.setUserNotificationPreference(request);
            if (!response.getSuccess()) {
                throw new RuntimeException("Failed to update notification preference: " + response.getErrorMessage());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to update notification preference via notifications-service", e);
        }
    }

    public Boolean getUserDeviceNotificationEnabled(Long userId) {
        try {
            az.fitnest.notifications.grpc.GetDevicesByUserIdRequest request =
                az.fitnest.notifications.grpc.GetDevicesByUserIdRequest.newBuilder()
                    .setUserId(userId)
                    .build();
            az.fitnest.notifications.grpc.GetDevicesByUserIdResponse response =
                notificationsStub.getDevicesByUserId(request);
            if (response.getDevicesCount() > 0) {
                az.fitnest.notifications.grpc.Device device = response.getDevices(0);
                return device.getNotificationsEnabled();
            }
        } catch (Exception e) {
        }
        return true;
    }
}
