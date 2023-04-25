package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.NotificationActionType;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedNotification;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.repositories.StoredResolvedNotificationRepository;
import sk.stuba.sdg.isbe.services.NotificationService;
import sk.stuba.sdg.isbe.utilities.NotificationLevelUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private StoredResolvedNotificationRepository storedResolvedNotificationRepository;

    @Override
    public Notification createNotification(Notification notification) {

        Optional<Notification> notificationInDb = notificationRepository.getNotificationByName(notification.getName());

        if (notificationInDb.isPresent()) {
            throw new EntityExistsException("Notification with this name already exists.");
        }

        validateNotification(notification);

        notification.setCreatedAt(Instant.now().toEpochMilli());
        notification.setMutedUntil(null);
        notification.setForTimeCountingActivatedAt(null);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsAssociatedWithDevice(String deviceID) {

        if (deviceID == null || deviceID.isEmpty()) {
            throw new InvalidOperationException("deviceID needs to be set for this operation.");
        }

        List<Notification> notifications = notificationRepository.getNotificationByDevicesContainingAndDeactivated(deviceID, false);

        if (notifications.isEmpty()) {
            throw new EntityExistsException("Notification associated with deviceID: " + deviceID + " does not exists.");
        }
        return notifications;
    }

    @Override
    public List<Notification> getNotifications() {

        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getActiveNotifications() {

        return notificationRepository.getNotificationByDeactivated(false);
    }

    @Override
    public Notification getNotificationById(String notificationId) {

        validateNotificationId(notificationId);

        Notification notification = notificationRepository.getNotificationById(notificationId);

        if (notification == null) {
            throw new NotFoundCustomException("Notification with ID: '" + notificationId + "' was not found!");
        }

        return notification;
    }

    @Override
    public List<Notification> getNotificationByDeviceAndTag(String deviceId, String tag) {

        return notificationRepository.getNotificationByDeviceAndTag(deviceId, tag);
    }

    @Override
    public Notification editNotification(Notification notification) {

        validateNotificationId(notification.getId());

        Notification existingNotification = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new InvalidEntityException("Notification not found."));

        validateNotification(notification);

        existingNotification.setName(notification.getName());
        existingNotification.getDevices().clear();
        existingNotification.getDevices().addAll(notification.getDevices());
        existingNotification.setDeactivated(notification.getDeactivated());
        existingNotification.setRules(notification.getRules());
        existingNotification.setAlreadyTriggered(notification.getAlreadyTriggered());
        existingNotification.setMutedUntil(notification.getMutedUntil());
        existingNotification.getForTimeCountingActivatedAt().clear();
        existingNotification.getForTimeCountingActivatedAt().putAll(notification.getForTimeCountingActivatedAt());
        existingNotification.getMessageMultiplicityCounter().clear();
        existingNotification.getMessageMultiplicityCounter().putAll(notification.getMessageMultiplicityCounter());
        existingNotification.getMessageAndTriggerTime().clear();
        existingNotification.getMessageAndTriggerTime().putAll(notification.getMessageAndTriggerTime());
        existingNotification.getJobAndTriggerTime().clear();
        existingNotification.getJobAndTriggerTime().putAll(notification.getJobAndTriggerTime());
        existingNotification.getActiveAtDay().clear();
        existingNotification.getActiveAtDay().addAll(notification.getActiveAtDay());
        existingNotification.getActiveAtHour().clear();
        existingNotification.getActiveAtHour().addAll(notification.getActiveAtHour());

        return notificationRepository.save(existingNotification);
    }

    @Override
    public Notification deleteNotification(String notificationId) {

        validateNotificationId(notificationId);

        Notification notificationToDelete = getNotificationById(notificationId);
        notificationToDelete.setDeactivated(true);
        return notificationRepository.save(notificationToDelete);
    }

    @Override
    public Notification muteNotification(String notificationId, Integer minutes) {

        validateNotificationId(notificationId);

        if (minutes == null) {
            throw new InvalidOperationException("Mute time for notification needs to be defined.");
        }

        Notification notificationToMute = getNotificationById(notificationId);
        long muteTimeConverted = minutes * 60 * 1000;
        notificationToMute.setMutedUntil(Instant.now().plusMillis(muteTimeConverted).toEpochMilli());

        return notificationRepository.save(notificationToMute);
    }

    @Override
    public List<Notification> getNotificationsWithMessage() {

        List<Notification> notifications = notificationRepository.getNotificationByDeactivated(false);

        return notifications.stream()
                .filter(notification -> notification.getMessageAndTriggerTime() != null && !notification.getMessageAndTriggerTime().isEmpty())
                .toList();
    }

    @Override
    public Notification resolveNotification(String notificationId, String notificationLevel) {

        validateNotificationId(notificationId);
        Notification notificationToResolve = getNotificationById(notificationId);

        NotificationLevelEnum notificationLevelEnum = NotificationLevelUtils.getNotificationLevelEnum(notificationLevel);

        StoredResolvedNotification storedResolvedNotification = new StoredResolvedNotification();
        storedResolvedNotification.setNotificationId(notificationId);
        storedResolvedNotification.setCreatedAt(Instant.now().toEpochMilli());
        storedResolvedNotification.setLevel(notificationLevelEnum);
        storedResolvedNotification.getMessageAndTriggerTime().putAll(notificationToResolve.getMessageAndTriggerTime());
        storedResolvedNotification.getMessageMultiplicityCounter().putAll(notificationToResolve.getMessageMultiplicityCounter());
        storedResolvedNotification.setNotificationActionType(NotificationActionType.MESSAGE);

        notificationToResolve.setAlreadyTriggered(false);
        notificationToResolve.getMessageAndTriggerTime().clear();
        notificationToResolve.getMessageMultiplicityCounter().clear();

        notificationRepository.save(notificationToResolve);

        storedResolvedNotificationRepository.save(storedResolvedNotification);

        return notificationToResolve;
    }

    @Override
    public void storeNotificationJobTriggers() {

        List<Notification> notifications = notificationRepository.findAllWithJobAndTriggerTime();

        List<StoredResolvedNotification> storedResolvedNotificationList = new ArrayList<>();
        for (Notification notification : notifications) {
            StoredResolvedNotification storedResolvedNotification = new StoredResolvedNotification();
            storedResolvedNotification.setNotificationId(notification.getId());
            storedResolvedNotification.setCreatedAt(Instant.now().toEpochMilli());
            storedResolvedNotification.setLevel(null);
            storedResolvedNotification.getJobAndTriggerTime().putAll(notification.getJobAndTriggerTime());
            storedResolvedNotification.setNotificationActionType(NotificationActionType.JOB);

            storedResolvedNotificationList.add(storedResolvedNotification);

            notification.setAlreadyTriggered(false);
            notification.getJobAndTriggerTime().clear();
        }

        if (!storedResolvedNotificationList.isEmpty()) {
            storedResolvedNotificationRepository.saveAll(storedResolvedNotificationList);
            notificationRepository.saveAll(notifications);
        }
    }

    @Override
    public void validateNotification(Notification notification) {

        if (!notification.hasNonEmptyName()) {
            throw new InvalidEntityException("Notification name needs to be set correctly.");
        } else if (!notification.hasNonEmptyDevices()) {
            throw new InvalidEntityException("Notification devices needs to be set correctly.");
        } else if (notification.getDeactivated() == null) {
            throw new InvalidEntityException("Notification activity needs to be set correctly.");
        } else if (!notification.hasNonEmptyRules()) {
            throw new InvalidEntityException("Notification rules needs to be set correctly.");
        }
    }

    @Override
    public void validateNotificationId(String notificationId) {

        if (notificationId == null || notificationId.isEmpty()) {
            throw new InvalidEntityException("Notification id is not valid.");
        }
    }
}
