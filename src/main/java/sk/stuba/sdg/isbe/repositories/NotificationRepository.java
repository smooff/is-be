package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sk.stuba.sdg.isbe.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Optional<Notification> getNotificationByName(String name);

   List<Notification> getNotificationByDevicesContainingAndDeactivated(String device, boolean deactivated);

    List<Notification> getNotificationByDeactivated(boolean deactivated);

   Notification getNotificationById(String id);

   @Query("{'deviceAndTag.?0': ?1}")
   List<Notification> getNotificationByDeviceAndTag(String device, String tag);

    @Query("{ 'jobAndTriggerTime' : { $exists : true, $ne : {} } }")
    List<Notification> findAllWithJobAndTriggerTime();

}
