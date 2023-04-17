package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import sk.stuba.sdg.isbe.domain.enums.NotificationActionType;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedNotification;

import java.util.List;

public interface StoredResolvedNotificationRepository extends MongoRepository<StoredResolvedNotification, String> {

    List<StoredResolvedNotification> findByNotificationActionType(NotificationActionType notificationActionType);

    List<StoredResolvedNotification> findByCreatedAtLessThan(long timestamp);
}
