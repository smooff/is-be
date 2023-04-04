package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.CommandController;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.controllers.RecipeController;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.NotificationService;

import java.time.Instant;
import java.util.Arrays;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private JobController jobController;

    @Autowired
    private CommandController commandController;

    @Autowired
    NotificationService notificationService;
//    @Bean
//    void addRecipes() {
//        Recipe active = new Recipe();
//        active.setName("activeRecipe " + Instant.now().toEpochMilli());
//        active.setDeactivated(false);
//        active.setTypeOfDevice(DeviceTypeEnum.ESP32);
//        active.setSubRecipe(false);
//    }

//    @Bean
//    void addNotification(){
//        Notification notification = new Notification("testovacia notifikacie", Arrays.asList("z1_ID", "z3_ID"), true, "rulezzzz");
//        notificationService.createNotification(notification);

//        notificationService.getNotificationsAssociatedWithDevice("z1_ID");
//    }
}
