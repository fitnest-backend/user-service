package az.fitnest.user.client;

import az.fitnest.notifications.grpc.GetNotificationsRequest;
import az.fitnest.notifications.grpc.GetNotificationsResponse;
import az.fitnest.notifications.grpc.NotificationsServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationsGrpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NotificationsGrpcClient.class);

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
                String errorMsg = response.getErrorMessage();
                if ("No current device found for user".equals(errorMsg)) {
                    throw new az.fitnest.user.exception.BadRequestException("error.notification.no_current_device");
                }
                throw new RuntimeException("Failed to update notification preference: " + errorMsg);
            }
        } catch (az.fitnest.user.exception.BadRequestException e) {
            throw e;
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
                for (int i = 0; i < response.getDevicesCount(); i++) {
                    az.fitnest.notifications.grpc.Device d = response.getDevices(i);
                    logger.info("[getUserDeviceNotificationEnabled] userId={}, deviceId={}, isCurrent={}, notificationsEnabled={}", userId, d.getDeviceId(), d.getIsCurrent(), d.getNotificationsEnabled());
                }
                for (int i = 0; i < response.getDevicesCount(); i++) {
                    az.fitnest.notifications.grpc.Device d = response.getDevices(i);
                    if (d.getIsCurrent()) {
                        logger.info("[getUserDeviceNotificationEnabled] Selected deviceId={} (isCurrent=true) for notificationsEnabled={}", d.getDeviceId(), d.getNotificationsEnabled());
                        return d.getNotificationsEnabled();
                    }
                }
                az.fitnest.notifications.grpc.Device d = response.getDevices(0);
                logger.warn("[getUserDeviceNotificationEnabled] No device marked isCurrent, using first deviceId={} for notificationsEnabled={}", d.getDeviceId(), d.getNotificationsEnabled());
                return d.getNotificationsEnabled();
            }
        } catch (Exception e) {
            logger.error("[getUserDeviceNotificationEnabled] Exception for userId={}: {}", userId, e.getMessage(), e);
        }
        return true;
    }
}
