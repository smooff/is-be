package sk.stuba.sdg.isbe.controllers;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Notification;
import sk.stuba.sdg.isbe.services.NotificationService;

import java.util.List;

@RestController
@RequestMapping("api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;

//        this.notificationService.createCustomer(new CustomerCreateRequestDto("Simon","0908123456","mail@mail.com"));
    }

    @GetMapping
    public List<Notification> getNotifications() {
        return this.notificationService.getNotifications();
    }

    @PostMapping(value = "/create")
    public Notification createNotification(@Valid @RequestBody Notification notification) {
        return this.notificationService.createNotification(notification);
    }

    @PostMapping(value = "/getNotificationById/{notificationId}")
    public List<Notification> getNotificationById(@PathVariable("notificationId") String id) {
        return this.notificationService.getNotificationById(id);
    }

    @PostMapping(value = "/getNotificationByDevice/{deviceId}")
    public List<Notification> getNotificationByDevice(@PathVariable("deviceId") String id) {
        return this.notificationService.getNotificationsAssociatedWithDevice(id);
    }

    @PutMapping(value = "/edit")
    public Notification editNotification(@Valid @RequestBody Notification notification){
        return this.notificationService.editNotification(notification);
    }
}
