package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("!deployment")
public class CommandServiceTests {
    @Autowired
    private CommandService commandService;
    @Autowired
    private CommandRepository commandRepository;
    @Autowired
    private RecipeService recipeService;
    @Autowired
    private RecipeRepository recipeRepository;

    @Test
    void testCreateCommand() {
        Command command = new Command();
        Exception exception = assertThrows(InvalidEntityException.class, () -> {
            commandService.createCommand(command);
        });
        String expected = "Name of the command is not valid!";
        assertEquals(expected, exception.getMessage());

        command.setName("command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        exception = assertThrows(EntityExistsException.class, () -> {
            commandService.createCommand(command);
        });
        expected = "Command with name: '" + command.getName() + "' already exists!";
        assertEquals(expected, exception.getMessage());

        command.setParams(null);
        command.setName(command.getName() + "1");
        exception = assertThrows(InvalidEntityException.class, () -> {
            commandService.createCommand(command);
        });
        expected = "Command does not contain any parameters!";
        assertEquals(expected, exception.getMessage());

        commandRepository.delete(command);
    }

    @Test
    void testDeleteCommand() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        commandService.deleteCommand(command.getId());
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            commandService.deleteCommand(command.getId());
        });
        String expected = "Command with ID: '" + command.getId() + "' was not found!";
        assertEquals(expected, exception.getMessage());

        commandService.createCommand(command);
        Recipe recipe = new Recipe();
        recipe.setSubRecipe(false);
        recipe.setCommands(List.of(command));
        recipe.setName("recipeUsingCommand " + Instant.now().toEpochMilli());
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);
        Recipe recipe2 = new Recipe();
        recipe2.setSubRecipe(false);
        recipe2.setCommands(List.of(command));
        recipe2.setName("recipeUsingCommand1 " + Instant.now().toEpochMilli());
        recipe2.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe2);

        exception = assertThrows(InvalidOperationException.class, () -> {
            commandService.deleteCommand(command.getId());
        });
        expected = "Command is used in Recipes: \n" + String.join("\n", recipe.getName(), recipe2.getName()) + "\nRemove commands from recipes to be able to delete them!";
        assertEquals(expected, exception.getMessage());

        commandRepository.delete(command);
        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testGetCommand() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setDeactivated(true);
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            commandService.getCommandById(command.getId());
        });
        String expected = "Command with ID: '" + command.getId() + "' was not found!";
        assertEquals(expected, exception.getMessage());

        command.setDeactivated(false);
        commandService.createCommand(command);
        commandService.getCommandById(command.getId());
        commandRepository.delete(command);
    }

    @Test
    void testGetCommandByName() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        String fakeName = command.getName() + " fake";
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            commandService.getCommandByName(fakeName);
        });
        String expected = "Command with name: '" + fakeName + "' not found!";
        assertEquals(expected, exception.getMessage());

        commandService.getCommandByName(command.getName());
        commandRepository.delete(command);
    }

    @Test
    void testGetCommandsByDeviceType() {
        Command command = new Command();
        command.setName("command " + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        List<Command> commandsDb = commandService.getCommandsByDeviceType("ESP32");
        assertFalse(commandsDb.isEmpty());

        commandRepository.delete(command);
    }

}
