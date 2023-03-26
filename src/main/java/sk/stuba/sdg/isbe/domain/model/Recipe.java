package sk.stuba.sdg.isbe.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

import java.util.List;

@Document
public class Recipe {
    @Id
    private String id;
    private String name;
    private List<String> commandIds;
    private List<String> subRecipeIds;
    private DeviceTypeEnum typeOfDevice;
    private Boolean isSubRecipe;
    private boolean deactivated;

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

    public List<String> getCommandIds() {
        return commandIds;
    }

    public void setCommandIds(List<String> commandIds) {
        this.commandIds = commandIds;
    }

    public DeviceTypeEnum getTypeOfDevice() {
        return typeOfDevice;
    }

    public void setTypeOfDevice(DeviceTypeEnum typeOfDevice) {
        this.typeOfDevice = typeOfDevice;
    }

    public Boolean isSubRecipe() {
        return isSubRecipe;
    }

    public void setSubRecipe(Boolean subRecipe) {
        isSubRecipe = subRecipe;
    }

    public List<String> getSubRecipeIds() {
        return subRecipeIds;
    }

    public void setSubRecipeIds(List<String> subRecipeIds) {
        this.subRecipeIds = subRecipeIds;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }
}
