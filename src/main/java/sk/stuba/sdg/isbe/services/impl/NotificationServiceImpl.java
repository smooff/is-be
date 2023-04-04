package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.repositories.NotificationRepository;
import sk.stuba.sdg.isbe.services.NotificationService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Override
    public Notification createNotification(Notification notification) {

        Optional<Notification> notificationInDb = notificationRepository.getNotificationByName(notification.getName());

        if (notificationInDb.isPresent()) {
            throw new EntityExistsException("Notification with this name already exists.");
        }

        validateNotification(notification);

        notification.setCreatedAt(Instant.now().toEpochMilli());
        notification.setCounter(0);
        notification.setLevel(NotificationLevelEnum.NOT_SOLVED);

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsAssociatedWithDevice(String deviceID) {

        if(deviceID == null || deviceID.isEmpty()){
            throw new InvalidOperationException("deviceID needs to be set for this operation.");
        }

        List<Notification> notifications = notificationRepository.getNotificationByDevicesContaining(deviceID);

        if (notifications.isEmpty()) {
            throw new EntityExistsException("Notification associated with deviceID: "+ deviceID +" does not exists.");
        }
        return notifications;
    }

    @Override
    public List<Notification> getNotifications() {

        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationById(String notificationId) {

        validateNotificationId(notificationId);

        return notificationRepository.getNotificationById(notificationId);
    }

    @Override
    public Notification editNotification(Notification notification) {

        validateNotificationId(notification.getId());

        Notification existingNotification = notificationRepository.findById(notification.getId())
                .orElseThrow(() -> new InvalidEntityException("Notification not found."));

        validateNotification(notification);

        existingNotification.setName(notification.getName());
        existingNotification.setDevices(notification.getDevices());
        existingNotification.setActive(notification.getActive());
        existingNotification.setRules(notification.getRules());
        existingNotification.setLevel(notification.getLevel());

        return notificationRepository.save(existingNotification);
    }

    @Override
    public void validateNotification(Notification notification) {

        if (!notification.hasNonEmptyName()) {
            throw new InvalidEntityException("Notification name needs to be set correctly.");
        } else if (!notification.hasNonEmptyDevices()) {
            throw new InvalidEntityException("Notification devices needs to be set correctly.");
        } else if (notification.getActive() == null) {
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
