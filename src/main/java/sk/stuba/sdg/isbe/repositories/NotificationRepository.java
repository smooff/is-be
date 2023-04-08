package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import sk.stuba.sdg.isbe.domain.model.Notification;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    Optional<Notification> getNotificationByName(String name);

   List<Notification> getNotificationByDevicesContaining(String device);

   List<Notification> getNotificationById(String id);

   @Query("{'deviceAndTag.?0': ?1}")
   List<Notification> getNotificationByDeviceAndTag(String device, String tag);

}
