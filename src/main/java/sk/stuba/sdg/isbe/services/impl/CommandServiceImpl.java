package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    private CommandRepository commandRepository;

    @Autowired
    private RecipeService recipeService;

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

        commandRepository.save(command);
        return command;
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
    public Command getCommandByName(String name) {
        Command command = commandRepository.getCommandByNameAndDeactivated(name, false);
        if (command == null) {
            throw new NotFoundCustomException("Command with name: '" + name + "' not found!");
        }
        return command;
    }

    @Override
    public Command deleteCommand(String commandId) {
        Command command = getCommandById(commandId);
        List<Recipe> recipes = recipeService.getAllRecipes();
        List<String> recipeNames = new ArrayList<>();

        for (Recipe recipe : recipes) {
            if (recipe.getCommandIds() != null) {
                for (String recipeCommandId : recipe.getCommandIds()) {
                    if (recipeCommandId.equals(commandId)) {
                        recipeNames.add(recipe.getName());
                    }
                }
            }
        }
        if (!recipeNames.isEmpty()) {
            throw new InvalidOperationException("Command is used in Recipes: \n" + String.join("\n", recipeNames) + "\nRemove commands from recipes to be able to delete them!");
        }
        command.setDeactivated(true);
        commandRepository.save(command);
        return command;
    }

    private boolean commandWithNameExists(String name) {
        return commandRepository.getCommandByNameAndDeactivated(name, false) != null;
    }
}
