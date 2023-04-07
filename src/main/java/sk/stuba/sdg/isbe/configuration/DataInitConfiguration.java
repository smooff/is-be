package sk.stuba.sdg.isbe.configuration;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.services.NotificationService;

@Configuration
public class DataInitConfiguration {

    @Autowired
    NotificationService notificationService;

    @Value("${test.properties.value}")
    private String testPropertiesValue;

    @PostConstruct
    public void testPropFile(){
        System.out.println("props:"+testPropertiesValue);
    }

//    @Bean
//    void addNotification(){
//        Notification notification = new Notification("testovacia notifikacie", Arrays.asList("z1_ID", "z3_ID"), true, "rulezzzz");
//        notificationService.createNotification(notification);

//        notificationService.getNotificationsAssociatedWithDevice("z1_ID");
//    }
}
