package sk.stuba.sdg.isbe.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

import java.util.List;

@Document
public class Command {

    @Id
    private String id;

    /**
     * Command's name.
     */
    private String name;

    /**
     * Parameters which are executed by the device. These are given by the user when creating a command.
     */
    private List<Integer> params;

    /**
     * Device type on which the commands can be executed. Also helps when adding command to a recipe to prevent user from adding a non-supported command.
     */
    private DeviceTypeEnum deviceType;

    /**
     * Command's creation date.
     */
    private long createdAt;

    /**
     * Flag that is set when the user deletes a command. It provides the option to retrieve deleted commands from the database.
     */
    @JsonIgnore
    private boolean deactivated;


    public Command() {}

    public Command(String name, List<Integer> params, DeviceTypeEnum deviceType) {
        this.name = name;
        this.params = params;
        this.deviceType = deviceType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getParams() {
        return params;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
