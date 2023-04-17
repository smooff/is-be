package sk.stuba.sdg.isbe.scheduler;


import com.mongodb.client.MongoClient;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.services.NotificationService;
import sk.stuba.sdg.isbe.services.StoredResolvedNotificationService;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;


@Component
public class StoredResolvedNotificationScheduler {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    StoredResolvedNotificationService storedResolvedNotificationService;

    @Bean
    public LockProvider lockProvider(MongoClient mongo) {
        return new MongoLockProvider(mongo.getDatabase("is-sdg-database"));
    }

    @Scheduled(cron = "15 * * * * *")
    @SchedulerLock(name = "scheduledTaskName", lockAtMostFor = "14m", lockAtLeastFor = "14m")
    public void removeOldStoredNotificationData() {
        System.out.println("TEST LOCKER");
    }

    // execute every 24 hours
//    @Scheduled(cron = "0 0 0 * * ?")
//    public void storeNotificationJobTriggers() {
//        this.notificationService.storeNotificationJobTriggers();
//    }

    // execute once every week - Monday
//    @Scheduled(cron = "0 0 0 * * 1")
//    @SchedulerLock(name = "scheduledTaskName", lockAtMostForString = "PT14M", lockAtLeastForString = "PT14M")
//    public void removeOldStoredNotificationData() {
//        storedResolvedNotificationService.removeOldStoredNotificationData();
//    }

}
