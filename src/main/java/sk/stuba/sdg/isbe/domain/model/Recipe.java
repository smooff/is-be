package sk.stuba.sdg.isbe.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;

import java.util.List;

@Document
public class Recipe {

    @Id
    private String id;

    /**
     * Recipe's name.
     */
    private String name;

    /**
     * Commands which have to be executed when the recipe is run as job.
     */
    @DBRef
    private List<Command> commands;

    /**
     * All recipes are able to contain sub-recipes, so that it is easier for the user to create new recipes.
     */
    @DBRef
    private List<Recipe> subRecipes;

    /**
     * Device type on which the recipe can be run as a job.
     */
    private DeviceTypeEnum deviceType;

    /**
     * Flag that indicates if the recipe is only a sub-recipe. Sub-recipe can't be run as stand-alone.
     */
    private Boolean subRecipe;

    /**
     * Recipe's creation date - set when the user saves the recipe.
     */
    private long createdAt;

    /**
     * Flag that is set when the user deletes a recipe. It provides the option to retrieve deleted recipes from the database.
     */
    @JsonIgnore
    private boolean deactivated;

    public Recipe() {}

    public Recipe(String name, DeviceTypeEnum deviceType, Boolean subRecipe) {
        this.name = name;
        this.deviceType = deviceType;
        this.subRecipe = subRecipe;
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

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public DeviceTypeEnum getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceTypeEnum deviceType) {
        this.deviceType = deviceType;
    }

    public Boolean isSubRecipe() {
        return subRecipe;
    }

    public void setSubRecipe(Boolean subRecipe) {
        this.subRecipe = subRecipe;
    }

    public List<Recipe> getSubRecipes() {
        return subRecipes;
    }

    public void setSubRecipes(List<Recipe> subRecipes) {
        this.subRecipes = subRecipes;
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
