package sk.stuba.sdg.isbe.entities.job;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
public class Recipe {
    @Id
    private String id;
    private String name;
    private List<Command> commands;
    private String typeOfDevice;
    private boolean isSubRecipe;

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

    public String getTypeOfDevice() {
        return typeOfDevice;
    }

    public void setTypeOfDevice(String typeOfDevice) {
        this.typeOfDevice = typeOfDevice;
    }

    public boolean isSubRecipe() {
        return isSubRecipe;
    }

    public void setSubRecipe(boolean subRecipe) {
        isSubRecipe = subRecipe;
    }
}
