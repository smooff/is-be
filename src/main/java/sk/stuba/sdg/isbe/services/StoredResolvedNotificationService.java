package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.StoredResolvedNotification;

import java.util.List;

public interface StoredResolvedNotificationService {

    List<StoredResolvedNotification> getStoredResolvedNotificationDataByJobAction();

    List<StoredResolvedNotification> getStoredResolvedNotificationDataByMessageAction();

    void removeOldStoredNotificationData();
}
