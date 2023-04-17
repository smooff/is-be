package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;
import sk.stuba.sdg.isbe.domain.enums.NotificationActionType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class StoredResolvedNotification {
    @Id
    private String id;

    private String notificationId;

    /**
     * Define type of the action that was executed after notification trigger.
     */
    private NotificationActionType notificationActionType;

    /**
     * Time of creation.
     */
    private Long createdAt;

    /**
     * Define notification level - response from user to notification.
     */
    private NotificationLevelEnum level;

    /**
     * This map holds all messages that notification returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> messageAndTriggerTime = new HashMap<>();

    /**
     * This map holds all messages that notification returned (triggered) and counter - how many times was message sent (triggered).
     */
    private Map<String, Integer> messageMultiplicityCounter = new HashMap<>();

    /**
     * This map holds all jobs that notification returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> jobAndTriggerTime = new HashMap<>();
    public String getId() {
        return id;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public NotificationLevelEnum getLevel() {
        return level;
    }

    public void setLevel(NotificationLevelEnum level) {
        this.level = level;
    }

    public Map<String, List<Long>> getMessageAndTriggerTime() {
        return messageAndTriggerTime;
    }

    public void setMessageAndTriggerTime(Map<String, List<Long>> messageAndTriggerTime) {
        this.messageAndTriggerTime = messageAndTriggerTime;
    }

    public Map<String, Integer> getMessageMultiplicityCounter() {
        return messageMultiplicityCounter;
    }

    public Map<String, List<Long>> getJobAndTriggerTime() {
        return jobAndTriggerTime;
    }

    public void setJobAndTriggerTime(Map<String, List<Long>> jobAndTriggerTime) {
        this.jobAndTriggerTime = jobAndTriggerTime;
    }

    public void setMessageMultiplicityCounter(Map<String, Integer> messageMultiplicityCounter) {
        this.messageMultiplicityCounter = messageMultiplicityCounter;
    }

    public NotificationActionType getNotificationActionType() {
        return notificationActionType;
    }

    public void setNotificationActionType(NotificationActionType notificationActionType) {
        this.notificationActionType = notificationActionType;
    }
}
