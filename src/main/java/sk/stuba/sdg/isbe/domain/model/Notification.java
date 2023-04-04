package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.NotificationLevelEnum;

import java.util.List;

@Document
public class Notification {

    public Notification(){}

    public Notification(String name, List<String> devices, Boolean active, String rules){
        this.name = name;
        this.devices = devices;
        this.active = active;
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
    private Boolean active;

    /**
     * Define notification level - response from user to notfication.
     */
    private NotificationLevelEnum level;

    /**
     * Spam counter for already sent notification.
     */
    private Integer counter;

    /**
     * Time of creation.
     */
    private Long createdAt;

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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public NotificationLevelEnum getLevel() {
        return level;
    }

    public void setLevel(NotificationLevelEnum level) {
        this.level = level;
    }

    public Integer getCounter() {
        return counter;
    }

    public void setCounter(Integer counter) {
        this.counter = counter;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id='" + id + '\'' +
                ", rules='" + rules + '\'' +
                ", name='" + name + '\'' +
                ", devices=" + devices +
                ", active=" + active +
                ", level=" + level +
                ", counter=" + counter +
                ", createdAt=" + createdAt +
                '}';
    }
}
