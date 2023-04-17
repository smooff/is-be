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

    private static final String NONE = "NONE";

    @Test
    void testCreateCommand() {
        Exception exception = assertThrows(InvalidEntityException.class, () -> commandService.createCommand(new Command()));
        assertEquals("Name of the command is not valid!", exception.getMessage());

        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        exception = assertThrows(EntityExistsException.class, () -> commandService.createCommand(command));
        assertEquals("Command with name: '" + command.getName() + "' already exists!", exception.getMessage());

        command.setParams(null);
        command.setName(command.getName() + "1");
        exception = assertThrows(InvalidEntityException.class, () -> commandService.createCommand(command));
        assertEquals("Command does not contain any parameters!", exception.getMessage());

        commandRepository.delete(command);
    }

    @Test
    void testUpdateCommand() {
        Command existingCommand = new Command("existingCommand" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(existingCommand);
        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        Exception exception = assertThrows(InvalidEntityException.class, () -> commandService.updateCommand(command.getId(), null));
        assertEquals("Command with changes is null!", exception.getMessage());

        exception = assertThrows(EntityExistsException.class, () -> commandService.updateCommand(command.getId(), existingCommand));
        assertEquals("Command with name: '" + existingCommand.getName() + "' already exists!", exception.getMessage());

        commandRepository.delete(command);
        commandRepository.delete(existingCommand);
    }

    @Test
    void testDeleteCommand() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        commandService.deleteCommand(command.getId());
        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.deleteCommand(command.getId()));
        assertEquals("Command with ID: '" + command.getId() + "' was not found!", exception.getMessage());

        commandService.createCommand(command);
        Recipe recipe = new Recipe("recipeUsingCommand " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);
        recipeService.addCommandToRecipe(recipe.getId(), command.getId());

        Recipe recipe2 = new Recipe("recipeUsingCommand1 " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe2);
        recipeService.addCommandToRecipe(recipe2.getId(), command.getId());

        exception = assertThrows(InvalidOperationException.class, () -> commandService.deleteCommand(command.getId()));
        assertEquals("Command is used in Recipes: " + String.join(", ", recipe.getName(), recipe2.getName()) +
                              ". Remove this command from recipes to be able to delete it!",
                               exception.getMessage());

        recipeService.removeCommandFromRecipe(recipe.getId(), command.getId(), 0);
        recipeService.removeCommandFromRecipe(recipe2.getId(), command.getId(), 0);
        commandService.deleteCommand(command.getId());

        commandRepository.delete(command);
        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testGetCommand() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1, 2, 3), DeviceTypeEnum.ESP32);
        command.setDeactivated(true);
        commandService.createCommand(command);
        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.getCommandById(command.getId()));
        assertEquals("Command with ID: '" + command.getId() + "' was not found!", exception.getMessage());

        command.setDeactivated(false);
        commandService.createCommand(command);
        commandService.getCommandById(command.getId());
        commandRepository.delete(command);
    }

    @Test
    void testGetCommandByName() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        String fakeName = command.getName() + " fake";
        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.getCommandByName(fakeName));
        assertEquals("Command with name: '" + fakeName + "' not found!", exception.getMessage());

        commandService.getCommandByName(command.getName());
        commandRepository.delete(command);
    }

    @Test
    void testGetCommandsByDeviceType() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        Command command2 = new Command("command2 " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.SDG_CUBE);
        commandService.createCommand(command2);

        List<Command> espCommands = commandService.getCommandsByDeviceType(DeviceTypeEnum.ESP32.name(), NONE, NONE);
        assertFalse(espCommands.isEmpty());
        assertTrue(espCommands.stream().allMatch(c -> c.getTypeOfDevice() == DeviceTypeEnum.ESP32));

        List<Command> sdgCommands = commandService.getCommandsByDeviceType(DeviceTypeEnum.SDG_CUBE.name(), NONE, NONE);
        assertFalse(sdgCommands.isEmpty());
        assertTrue(sdgCommands.stream().allMatch(c -> c.getTypeOfDevice() == DeviceTypeEnum.SDG_CUBE));

        commandRepository.delete(command);
        commandRepository.delete(command2);
    }

    @Test
    void testGetCommandsByDeviceTypePageable() {
        Command command = new Command("command " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        Command command2 = new Command("command2 " + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command2);

        List<Command> commands;
        commands = commandService.getCommandsByDeviceTypePageable("ESP32", 1, 2, NONE, NONE);
        assertEquals(2, commands.size());

        commands = commandService.getCommandsByDeviceTypePageable("ESP32", 1, 1, NONE, NONE);
        assertEquals(1, commands.size());

        Exception exception = assertThrows(NotFoundCustomException.class, () -> commandService.getCommandsByDeviceTypePageable("ESP32", 10000000, 1, NONE, NONE));
        assertEquals("There are not any commands with type of device: '" + "ESP32" + "' on page " + 10000000 + "!", exception.getMessage());

        commandRepository.delete(command);
        commandRepository.delete(command2);
    }

}
