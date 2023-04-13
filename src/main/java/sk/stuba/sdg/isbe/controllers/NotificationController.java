package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
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
    }

    @GetMapping("/all")
    @Operation(summary = "Get all Notifications")
    public List<Notification> getNotifications() {
        return this.notificationService.getNotifications();
    }

    @GetMapping("/active")
    @Operation(summary = "Get only active Notifications")
    public List<Notification> getActiveNotifications() {
        return this.notificationService.getActiveNotifications();
    }

    @PostMapping(value = "/create")
    @Operation(summary = "Create new Notification")
    public Notification createNotification(@Valid @RequestBody Notification notification) {
        return this.notificationService.createNotification(notification);
    }

    @PostMapping(value = "/getNotificationById/{notificationId}")
    @Operation(summary = "Get Notification by id")
    public Notification getNotificationById(@PathVariable("notificationId") String id) {
        return this.notificationService.getNotificationById(id);
    }

    @PostMapping(value = "/getNotificationByDeviceIdAndTag/{deviceId}/{tag}")
    @Operation(summary = "Get Notification by DeviceId and Tag (Tag in DataPointTag)")
    public List<Notification> getNotificationByDeviceAndTag(@PathVariable("deviceId") String deviceId, @PathVariable("tag") String tag) {
        return this.notificationService.getNotificationByDeviceAndTag(deviceId, tag);
    }

    @PostMapping(value = "/getNotificationByDeviceId/{deviceId}")
    @Operation(summary = "Get Notification by associated Device id")
    public List<Notification> getNotificationByDevice(@PathVariable("deviceId") String id) {
        return this.notificationService.getNotificationsAssociatedWithDevice(id);
    }

    @PutMapping(value = "/edit")
    @Operation(summary = "Edit existing Notification")
    public Notification editNotification(@Valid @RequestBody Notification notification){
        return this.notificationService.editNotification(notification);
    }

    @Operation(summary = "Delete Notification by uid")
    @DeleteMapping("/delete/{notificationId}")
    public Notification deleteNotification(@PathVariable String notificationId) {
        return this.notificationService.deleteNotification(notificationId);
    }

    @PostMapping(value = "/mute/{notificationId}/{minutes}")
    @Operation(summary = "Mute notification evaluation for certain time - minutes.")
    public Notification muteNotification(@PathVariable("notificationId") String notificationId, @PathVariable("minutes") Integer minutes) {
        return this.notificationService.muteNotification(notificationId, minutes);
    }

    @GetMapping(value = "/getNotificationsWithMessage")
    @Operation(summary = "Get all Notifications with some message for user - triggered Notifications.")
    public List<Notification> getNotificationsWithMessage() {
        return this.notificationService.getNotificationsWithMessage();
    }
}
