package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stuba.sdg.isbe.domain.model.StoredResolvedNotification;
import sk.stuba.sdg.isbe.services.StoredResolvedNotificationService;

import java.util.List;

@RestController
@RequestMapping("api/storedResolvedNotification")
public class StoredResolvedNotificationController {

    @Autowired
    StoredResolvedNotificationService storedResolvedNotificationService;

    @GetMapping("/job")
    @Operation(summary = "Get all Stored data for resolved Notifications by action type Job.")
    public List<StoredResolvedNotification> getStoredResolvedNotificationDataByJobAction() {
        return this.storedResolvedNotificationService.getStoredResolvedNotificationDataByJobAction();
    }

    @GetMapping("/message")
    @Operation(summary = "Get all Stored data for resolved Notifications by action type Message.")
    public List<StoredResolvedNotification> getStoredResolvedNotificationDataByMessageAction() {
        return this.storedResolvedNotificationService.getStoredResolvedNotificationDataByMessageAction();
    }
}
