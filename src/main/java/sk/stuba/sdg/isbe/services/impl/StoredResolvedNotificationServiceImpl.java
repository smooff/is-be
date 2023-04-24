package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.NotificationActionType;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedNotification;
import sk.stuba.sdg.isbe.repositories.StoredResolvedNotificationRepository;
import sk.stuba.sdg.isbe.services.StoredResolvedNotificationService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class StoredResolvedNotificationServiceImpl implements StoredResolvedNotificationService {

    @Autowired
    StoredResolvedNotificationRepository storedResolvedNotificationRepository;

    @Override
    public List<StoredResolvedNotification> getStoredResolvedNotificationDataByJobAction() {
        return storedResolvedNotificationRepository.findByNotificationActionType(NotificationActionType.JOB);
    }

    @Override
    public List<StoredResolvedNotification> getStoredResolvedNotificationDataByMessageAction() {
        return storedResolvedNotificationRepository.findByNotificationActionType(NotificationActionType.MESSAGE);
    }

    @Override
    public void removeOldStoredNotificationData() {

        long timeMinusWeek = Instant.now().minus(7, ChronoUnit.DAYS)
                .toEpochMilli();

        List<StoredResolvedNotification> storedResolvedNotificationList = storedResolvedNotificationRepository.findByCreatedAtLessThan(timeMinusWeek);

        if (!storedResolvedNotificationList.isEmpty()) {
            storedResolvedNotificationRepository.deleteAll(storedResolvedNotificationList);
        }
    }
}
