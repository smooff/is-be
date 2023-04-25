package sk.stuba.sdg.isbe.services;

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

    Notification resolveNotification(String notificationId, String notificationLevelEnum);

    void storeNotificationJobTriggers();

    public Notification setNotificationActiveAtHour(String notificationId, List<Integer> hours);

    public Notification setNotificationActiveAtDay(String notificationId, List<Integer> days);

    public Notification removeNotificationActiveAtHour(String notificationId);

    public Notification removeNotificationActiveAtDay(String notificationId);

    Notification editNotification(Notification notification);

    Notification deleteNotification(String notificationId);
}
