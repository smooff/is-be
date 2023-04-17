package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.RecipeService;
import sk.stuba.sdg.isbe.utilities.DeviceTypeUtils;
import sk.stuba.sdg.isbe.utilities.SortingUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private RecipeService recipeService;

    private static final String EMPTY_STRING = "";
    private static final String NONE = "NONE";

    @Override
    public Command createCommand(Command command) {
        if (command.getName() == null || command.getName().isEmpty()) {
            throw new InvalidEntityException("Name of the command is not valid!");
        }
        if (commandWithNameExists(command.getName())) {
            throw new EntityExistsException("Command with name: '" + command.getName() + "' already exists!");
        }
        if (command.getParams() == null || command.getParams().isEmpty()) {
            throw new InvalidEntityException("Command does not contain any parameters!");
        }
        if (command.getTypeOfDevice() == null) {
            throw new InvalidEntityException("Type of device for command must be set!");
        }

        command.setCreatedAt(Instant.now().toEpochMilli());

        return commandRepository.save(command);
    }

    @Override
    public Command getCommandById(String commandId) {
        Optional<Command> optionalCommand = commandRepository.getCommandByIdAndDeactivated(commandId, false);
        if (optionalCommand.isEmpty()) {
            throw new NotFoundCustomException("Command with ID: '" + commandId + "' was not found!");
        }
        return optionalCommand.get();
    }

    @Override
    public List<Command> getAllCommands(String sortBy, String sortDirection) {
        List<Command> commands = commandRepository.getCommandsByDeactivated(false, SortingUtils.getSort(Command.class, sortBy, sortDirection));
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database!");
        }
        return commands;
    }

    @Override
    public List<Command> getAllCommandsPageable(int page, int pageSize, String sortBy, String sortDirection) {
        Pageable pageable = SortingUtils.getPagination(Command.class, EMPTY_STRING, NONE, 1, 1);
        List<Command> commands = commandRepository.getCommandsByDeactivated(false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database yet!");
        }

        pageable = SortingUtils.getPagination(Command.class, sortBy, sortDirection, page, pageSize);
        commands = commandRepository.getCommandsByDeactivated(false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands on page " + page + "!");
        }
        return commands;
    }

    @Override
    public Command getCommandByName(String name) {
        Optional<Command> optionalCommand = commandRepository.getCommandByNameAndDeactivated(name, false);
        if (optionalCommand.isEmpty()) {
            throw new NotFoundCustomException("Command with name: '" + name + "' not found!");
        }
        return optionalCommand.get();
    }

    @Override
    public List<Command> getCommandsByDeviceType(String deviceType, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        List<Command> commands = commandRepository.getCommandsByTypeOfDeviceAndDeactivated(deviceTypeEnum, false, SortingUtils.getSort(Command.class, sortBy, sortDirection));
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands with this type of device in the database!");
        }
        return commands;
    }

    @Override
    public List<Command> getCommandsByDeviceTypePageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        Pageable pageable = SortingUtils.getPagination(Command.class, EMPTY_STRING, NONE, 1, 1);
        List<Command> commands = commandRepository.getCommandsByTypeOfDeviceAndDeactivated(deviceTypeEnum, false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands in the database of device type: " + deviceType + "!");
        }


        pageable = SortingUtils.getPagination(Command.class, sortBy, sortDirection, page, pageSize);
        commands = commandRepository.getCommandsByTypeOfDeviceAndDeactivated(deviceTypeEnum, false, pageable);
        if (commands.isEmpty()) {
            throw new NotFoundCustomException("There are not any commands with type of device: '" + deviceType + "' on page " + page + "!");
        }
        return commands;
    }

    @Override
    public Command deleteCommand(String commandId) {
        Command command = getCommandById(commandId);
        List<Recipe> recipesUsingCommand = recipeService.getRecipesContainingCommand(command);
        List<String> recipeNames = recipesUsingCommand.stream().map(Recipe::getName).toList();

        if (!recipeNames.isEmpty()) {
            throw new InvalidOperationException("Command is used in Recipes: " + String.join(", ", recipeNames)
                    + ". Remove this command from recipes to be able to delete it!");
        }

        command.setDeactivated(true);
        return commandRepository.save(command);
    }

    @Override
    public Command updateCommand(String commandId, Command updateCommand) {
        Command command = getCommandById(commandId);

        if (updateCommand == null) {
            throw new InvalidEntityException("Command with changes is null!");
        }
        if (!command.getName().equals(updateCommand.getName()) && commandWithNameExists(updateCommand.getName())) {
            throw new EntityExistsException("Command with name: '" + updateCommand.getName() + "' already exists!");
        }

        if (updateCommand.getName() != null) {
            command.setName(updateCommand.getName());
        }
        if (updateCommand.getParams() != null) {
            command.setParams(updateCommand.getParams());
        }
        if (updateCommand.getTypeOfDevice() != null) {
            List<Recipe> recipesUsingCommand = recipeService.getRecipesContainingCommand(command);
            List<String> recipeNames = recipesUsingCommand.stream().map(Recipe::getName).toList();

            if (!recipeNames.isEmpty()) {
                throw new InvalidOperationException("Command is used in Recipes: " + String.join(", ", recipeNames)
                        + ". Remove this command from recipes to be able to change its type!");
            }
            command.setTypeOfDevice(updateCommand.getTypeOfDevice());
        }

        return commandRepository.save(command);
    }

    private boolean commandWithNameExists(String name) {
        return commandRepository.getCommandByNameAndDeactivated(name, false).isPresent();
    }
}
