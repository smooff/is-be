package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Notification;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Notification notification);

    void validateNotification(Notification notification);

    void validateNotificationId(String notificationId);

    List<Notification> getNotificationsAssociatedWithDevice(String deviceID);

    List<Notification> getNotifications();

    List<Notification> getActiveNotifications();

    Notification getNotificationById(String notificationId);

    List<Notification> getNotificationByDeviceAndTag(String deviceId, String tag);

    Notification muteNotification(String notificationId, Integer minutes);

    List<Notification> getNotificationsWithMessage();

    Notification editNotification(Notification notification);

    Notification deleteNotification(String notificationId);
}
