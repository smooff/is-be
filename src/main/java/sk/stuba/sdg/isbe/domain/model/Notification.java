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
     * Define if the notification was already triggered - sent to user. This reset if users interact with notification.
     */
    private Boolean isAlreadyTriggered;

    /**
     * Define time until the notification is muted.
     */
    private Long mutedUntil;

    /**
     * Time of creation.
     */
    private Long createdAt;

    /**
     * Define if the notification (which contains forTime) has been triggered. Especially we need provide, which forTime return
     * statement was triggered (notification can have multiple forTime and each one of them needs own counter).
     * This map also define time when was notification first time triggered.
     * Final state (message for user/run job) of notification will trigger after
     * forTimeCounterActivatedAt+forTime(defined by user) has passed and all data sent to notification meet the
     * requirements to run the counting (requirements are NOT met, when data triggers forTimeReset - then
     * forTimeCountingActivatedAt resets).
     * This resets if notification is validating data, and these data does not meet the requirements of notification - then
     * notification's else statement of IF-check is executed and forTime is reset.
     * ForTime means: data validation in time - multiple data sent in certain time interval to trigger final state of notification.
     */
    private Map<String, Long> forTimeCountingActivatedAt = new HashMap<>();

    /**
     * This map holds all messages that notification returned (triggered) and time when trigger happened.
     */
    private Map<String, List<Long>> messageAndTriggerTime = new HashMap<>();

    /**
     * This map holds all messages that notification returned (triggered) and counter - how many times was message sent (triggered).
     */
    private Map<String, Integer> messageMultiplicityCounter = new HashMap<>();

    /**
     * This map holds all tags (DataPointTag) for every device used in certain notification.
     */
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

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getAlreadyTriggered() {
        return isAlreadyTriggered;
    }

    public void setAlreadyTriggered(Boolean alreadyTriggered) {
        isAlreadyTriggered = alreadyTriggered;
    }

    public Long getMutedUntil() {
        return mutedUntil;
    }

    public void setMutedUntil(Long mutedUntil) {
        this.mutedUntil = mutedUntil;
    }

    public Map<String, Long> getForTimeCountingActivatedAt() {
        return forTimeCountingActivatedAt;
    }

    public void setForTimeCountingActivatedAt(Map<String, Long> forTimeCountingActivatedAt) {
        this.forTimeCountingActivatedAt = forTimeCountingActivatedAt;
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

    public void setMessageMultiplicityCounter(Map<String, Integer> messageMultiplicityCounter) {
        this.messageMultiplicityCounter = messageMultiplicityCounter;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", rules='" + rules + '\'' +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                ", deactivated=" + deactivated +
                ", level=" + level +
                ", isAlreadyTriggered=" + isAlreadyTriggered +
                ", mutedUntil=" + mutedUntil +
                ", createdAt=" + createdAt +
                ", forTimeCountingActivatedAt=" + forTimeCountingActivatedAt +
                ", messageAndTriggerTime=" + messageAndTriggerTime +
                ", messageMultiplicityCounter=" + messageMultiplicityCounter +
                ", deviceAndTag=" + deviceAndTag +
                '}';
    }
}
