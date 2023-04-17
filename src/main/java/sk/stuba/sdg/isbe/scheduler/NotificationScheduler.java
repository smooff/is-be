package sk.stuba.sdg.isbe.scheduler;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.mongo.MongoLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sk.stuba.sdg.isbe.services.NotificationService;
import sk.stuba.sdg.isbe.services.StoredResolvedNotificationService;


@Component
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "5m")
public class NotificationScheduler {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    StoredResolvedNotificationService storedResolvedNotificationService;

    @Value("${spring.data.mongodb.database}")
    private String mongoDbName;

    @Bean
    public LockProvider lockProvider(MongoClient mongo) {
        MongoDatabase database = mongo.getDatabase(mongoDbName);
        return new MongoLockProvider(database);
    }

    // execute every 24 hours
    @Scheduled(cron = "0 0 0 * * ?")
    public void storeNotificationJobTriggers() {
        this.notificationService.storeNotificationJobTriggers();
    }

    // execute once every week - Monday
    @Scheduled(cron = "0 0 0 * * 1")
    @SchedulerLock(name = "scheduledTaskName", lockAtMostFor = "20m", lockAtLeastFor = "1m")
    public void removeOldStoredNotificationData() {
        storedResolvedNotificationService.removeOldStoredNotificationData();
    }

}
