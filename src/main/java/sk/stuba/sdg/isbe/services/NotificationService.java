package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Notification;

import java.util.List;

public interface NotificationService {

    Notification createNotification(Notification notification);

    void validateNotification(Notification notification);

    void validateNotificationId(String notificationId);

    List<Notification> getNotificationsAssociatedWithDevice(String deviceID);

    List<Notification> getNotifications();

    List<Notification> getNotificationById(String notificationId);

    Notification editNotification(Notification notification);
}
