package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.*;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CommandService commandService;

    @Override
    public Recipe createRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Recipe has no name set!");
        }

        if (activeRecipeWithNameExists(recipe.getName())) {
            throw new InvalidEntityException("Recipe with name: '" + recipe.getName() + "' already exists!");
        }

        if (recipe.getTypeOfDevice() == null || recipe.getTypeOfDevice().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Type of device for recipe is missing!");
        }

        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        return recipeRepository.getRecipesByTypeOfDeviceAndDeactivated(typeOfDevice, false);
    }

    @Override
    public Recipe getRecipeByName(String name) {
        Recipe recipe = recipeRepository.getRecipeByNameAndDeactivated(name, false);
        if (recipe == null) {
            throw new NotFoundCustomException("Recipe with name: '" + name + "' does not exist!");
        }
        return recipe;
    }

    @Override
    public ResponseEntity<Recipe> updateRecipe(String recipeId, Recipe changeRecipe) {
        Recipe recipe = getRecipe(recipeId);

        if (!recipe.getName().equals(changeRecipe.getName()) && activeRecipeWithNameExists(changeRecipe.getName())) {
            throw new EntityExistsException("Recipe with name: '" + changeRecipe.getName() + "' already exists!");
        }

        if (changeRecipe.getName() != null) {
            recipe.setName(changeRecipe.getName());
        }
        if (changeRecipe.getCommands() != null) {
            recipe.setCommands(changeRecipe.getCommands());
        }
        if (changeRecipe.getTypeOfDevice() != null) {
            recipe.setTypeOfDevice(changeRecipe.getTypeOfDevice());
        }
        if (changeRecipe.isSubRecipe() != null) {
            recipe.setSubRecipe(changeRecipe.isSubRecipe());
        }

        recipeRepository.save(recipe);
        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> addSubRecipeToRecipe(String recipeId, String subRecipeId) {
        Recipe recipe = getRecipe(recipeId);
        Recipe subRecipe = getRecipe(subRecipeId);

        if (!recipe.getTypeOfDevice().equals(subRecipe.getTypeOfDevice())) {
            throw new InvalidEntityException("Device types of the recipes do not match!");
        }

        if (recipe.getSubRecipes() == null) {
            recipe.setSubRecipes(new ArrayList<>());
        }

        recipe.getSubRecipes().add(subRecipe);
        recipeRepository.save(recipe);

        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> removeSubRecipeFromRecipe(String recipeId, String subRecipeId) {
        Recipe recipe = getRecipe(recipeId);
        if (subRecipeId == null) {
            throw new NullPointerException("Sub-recipe's ID is null!");
        }

        List<Recipe> subRecipesToRemove = new ArrayList<>();
        for (Recipe subRecipe : recipe.getSubRecipes()) {
            if (subRecipe.getId().equals(subRecipeId)) {
                subRecipesToRemove.add(subRecipe);
            }
        }
        if (subRecipesToRemove.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain the sub-recipe!");
        }

        recipe.getSubRecipes().removeAll(subRecipesToRemove);
        recipeRepository.save(recipe);

        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> deleteRecipe(String recipeId) {
        Recipe recipeToDelete = getRecipe(recipeId);
        recipeToDelete.setDeactivated(true);
        recipeRepository.save(recipeToDelete);
        return ResponseEntity.ok(recipeToDelete);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public List<Recipe> getFullRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(false, typeOfDevice, false);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(true, typeOfDevice, false);
    }

    @Override
    public Recipe getRecipe(String recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isEmpty()) {
            throw new NotFoundCustomException("Recipe with ID: '" + recipeId + "' was not found!");
        }
        return optionalRecipe.get();
    }

    @Override
    public Recipe addCommandToRecipe(String recipeId, String commandId) {
        Recipe recipe = getRecipe(recipeId);
        Command command = commandService.getCommandById(commandId, false);

        if (command.isDeactivated()) {
            throw new InvalidOperationException("Command is deactivated!");
        }

        if (command.getName() == null || command.getName().isEmpty()) {
            throw new InvalidOperationException("Command does not have any name set!");
        }
        if (command.getParams() == null || command.getParams().isEmpty()) {
            throw new InvalidOperationException("Command does not have any parameters!");
        }

        if (recipe.getCommands() == null) {
            recipe.setCommands(List.of(command));
        } else {
            recipe.getCommands().add(command);
        }
        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public Recipe removeCommandFromRecipe(String recipeId, String commandId) {
        Recipe recipe = getRecipe(recipeId);
        Command command = commandService.getCommandById(commandId, false);

        if (recipe.getCommands() == null && recipe.getCommands().isEmpty()) {
            throw new NotFoundCustomException("Command: '" + command.getName() + "' is not contained by this recipe!");
        }

        List<Command> commandsToRemove = new ArrayList<>();
        for (Command commandInRecipe : recipe.getCommands()) {
            if (commandInRecipe.getId().equals(commandId)) {
                commandsToRemove.add(commandInRecipe);
            }
        }

        recipe.getCommands().removeAll(commandsToRemove);
        recipeRepository.save(recipe);

        if (!commandsToRemove.isEmpty()) {
            return recipe;
        }
        throw new NotFoundCustomException("Recipe does not contain command with ID: " + command.getId());
    }

    private boolean activeRecipeWithNameExists(String name) {
        return recipeRepository.getRecipeByNameAndDeactivated(name, false) != null;
    }
}
