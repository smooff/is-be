package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document
public class Notification {

    public Notification() {
    }

    public Notification(String name, List<String> devices, Boolean deactivated, String rules) {
        this.name = name;
        this.devices = devices;
        this.deactivated = deactivated;
        this.rules = rules;
    }

    @Id
    private String id;

    /**
     * Rules (Expressions) defined in notification.
     */
    private String rules;

    /**
     * Notification name.
     */
    private String name;

    /**
     * Devices associated with notification.
     */
    private List<String> devices;

    /**
     * Define if notification is active.
     */
    private Boolean deactivated;

    /**
     * Define notification level - response from user to notfication.
     */
    private NotificationLevelEnum level;

    /**
     * Spam counter for already sent notification.
     */
    private Integer multiplicityCounter;

    /**
     * Define if the notification was already triggered - sent to user. This reset if users interact with notification.
     */
    private Boolean isAlreadyTriggered;

    /**
     * Message for user after notification is triggered.
     */
    private String notificationMessage;

    /**
     * Define time when was notification first time triggered.
     */
    private Long firstTimeTriggeredAt;

    /**
     * Define time when was notification last time triggered.
     */
    private Long lastTimeTriggeredAt;

    /**
     * Define time until the notification is muted.
     */
    private Long mutedUntil;

    /**
     * Time of creation.
     */
    private Long createdAt;

    /**
     * Define if the notification (which contains forTime) has time counter triggered.
     * This reset if notification is validating data, and these data does not meet the requirements of notification - then
     * forTime is reseted.
     * ForTime means: data validation in time - multiple data sent in certain time interval to trigger final state of notification.
     */
    private Boolean forTimeCounterAlreadyTriggered;

    /**
     * Define time when was notification (which contains forTime) first time triggered.
     * Final state (message for user/run job) of notification will trigger after
     * forTimeCounterActivatedAt+forTime(defined by user) has passed and all data sent to notification meet the
     * requirements to run the counter (requirements are NOT met, when data triggers forTimeReset - then
     * forTimeCounterActivatedAt and forTimeCounterAlreadyTriggered resets).
     */
    private Long forTimeCounterActivatedAt;

    private Map<String, List<String>> deviceAndTag = new HashMap<>();

    public Map<String, List<String>> getDeviceAndTag() {
        return deviceAndTag;
    }

    public void setDeviceAndTag(Map<String, List<String>> deviceAndTag) {
        this.deviceAndTag = deviceAndTag;
    }

    public boolean hasNonEmptyName() {
        return name != null && !name.isEmpty();
    }

    public boolean hasNonEmptyDevices() {
        return devices != null && devices.stream().allMatch(device -> !device.isEmpty());
    }

    public boolean hasNonEmptyRules() {
        return rules != null && !rules.isEmpty();
    }

    public String getId() {
        return id;
    }

    public String getRules() {
        return rules;
    }

    public void setRules(String rules) {
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public Boolean getDeactivated() {
        return deactivated;
    }

    public void setDeactivated(Boolean deactivated) {
        this.deactivated = deactivated;
    }

    public NotificationLevelEnum getLevel() {
        return level;
    }

    public void setLevel(NotificationLevelEnum level) {
        this.level = level;
    }

    public Integer getMultiplicityCounter() {
        return multiplicityCounter;
    }

    public void setMultiplicityCounter(Integer multiplicityCounter) {
        this.multiplicityCounter = multiplicityCounter;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public Boolean getAlreadyTriggered() {
        return isAlreadyTriggered;
    }

    public void setAlreadyTriggered(Boolean alreadyTriggered) {
        isAlreadyTriggered = alreadyTriggered;
    }

    public Long getFirstTimeTriggeredAt() {
        return firstTimeTriggeredAt;
    }

    public void setFirstTimeTriggeredAt(Long firstTimeTriggeredAt) {
        this.firstTimeTriggeredAt = firstTimeTriggeredAt;
    }

    public Long getLastTimeTriggeredAt() {
        return lastTimeTriggeredAt;
    }

    public void setLastTimeTriggeredAt(Long lastTimeTriggeredAt) {
        this.lastTimeTriggeredAt = lastTimeTriggeredAt;
    }

    public Long getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(Long mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    public Boolean getForTimeCounterAlreadyTriggered() {
        return forTimeCounterAlreadyTriggered;
    }

    public void setForTimeCounterAlreadyTriggered(Boolean forTimeCounterAlreadyTriggered) {
        this.forTimeCounterAlreadyTriggered = forTimeCounterAlreadyTriggered;
    }

    public Long getForTimeCounterActivatedAt() {
        return forTimeCounterActivatedAt;
    }

    public void setForTimeCounterActivatedAt(Long forTimeCounterActivatedAt) {
        this.forTimeCounterActivatedAt = forTimeCounterActivatedAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", rules='" + rules + '\'' +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                ", active=" + deactivated +
                ", level=" + level +
                ", counter=" + multiplicityCounter +
                ", createdAt=" + createdAt +
                '}';
    }
}
