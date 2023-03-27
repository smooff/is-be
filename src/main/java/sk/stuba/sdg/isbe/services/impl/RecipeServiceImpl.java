package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.RecipeService;
import sk.stuba.sdg.isbe.utilities.DeviceTypeUtils;

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
        if (recipeWithNameExists(recipe.getName())) {
            throw new InvalidEntityException("Recipe with name: '" + recipe.getName() + "' already exists!");
        }
        if (recipe.getTypeOfDevice() == null) {
            throw new InvalidEntityException("Type of device for recipe is missing!");
        }
        if (recipe.isSubRecipe() == null) {
            throw new InvalidEntityException("Recipe's sub-recipe status not defined!");
        }

        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        DeviceTypeEnum deviceType = DeviceTypeUtils.getDeviceTypeEnum(typeOfDevice);
        return recipeRepository.getRecipesByTypeOfDeviceAndDeactivated(deviceType, false);
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

        if (changeRecipe == null) {
            throw new InvalidEntityException("Recipe with changes is null!");
        }

        if (!recipe.getName().equals(changeRecipe.getName()) && recipeWithNameExists(changeRecipe.getName())) {
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
        if (changeRecipe.getSubRecipes() != null) {
            recipe.setSubRecipes(changeRecipe.getSubRecipes());
        }

        recipeRepository.save(recipe);
        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> addSubRecipeToRecipe(String recipeId, String subRecipeId) {
        Recipe recipe = getRecipe(recipeId);
        Recipe subRecipe = getRecipe(subRecipeId);

        if (recipe.getTypeOfDevice() != subRecipe.getTypeOfDevice()) {
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
    public ResponseEntity<Recipe> removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index) {
        Recipe recipe = getRecipe(recipeId);
        if (subRecipeId == null) {
            throw new NullPointerException("ID of sub-recipe must not be null!");
        }

        if (recipe.getSubRecipes() == null || recipe.getSubRecipes().isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipes!");
        }

        if (index < 0 || index > recipe.getSubRecipes().size() - 1) {
            throw new IndexOutOfBoundsException("Provided index of subRecipe is not valid!");
        }

        if (recipe.getSubRecipes().get(index).getId().equals(subRecipeId)) {
            recipe.getSubRecipes().remove(index);
            recipeRepository.save(recipe);
            return ResponseEntity.ok(recipe);
        }

        List<String> subRecipeIndexes = new ArrayList<>();
        int currentIndex = 0;
        for (Recipe subRecipeOfRecipe : recipe.getSubRecipes()) {
            if (subRecipeOfRecipe.getId().equals(subRecipeId)) {
                subRecipeIndexes.add(String.valueOf(currentIndex));
            }
            currentIndex++;
        }
        if (subRecipeIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipe with ID '" + subRecipeId + "' !");
        }

        throw new NotFoundCustomException("Sub-recipe not found on index: " + index + "!"
                                          + " Sub-recipes with this ID can be found on indexes: " + String.join(", ", subRecipeIndexes));
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
        DeviceTypeEnum deviceType = DeviceTypeUtils.getDeviceTypeEnum(typeOfDevice);
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(false, deviceType, false);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        DeviceTypeEnum deviceType = DeviceTypeUtils.getDeviceTypeEnum(typeOfDevice);
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(true, deviceType, false);
    }

    @Override
    public List<Recipe> getRecipesContainingCommand(Command command) {
        return recipeRepository.getRecipesByCommandsContaining(command);
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
        Command command = commandService.getCommandById(commandId);

        if (command.isDeactivated()) {
            throw new InvalidOperationException("Command is not active!");
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
    public Recipe removeCommandFromRecipe(String recipeId, String commandId, int index) {
        Recipe recipe = getRecipe(recipeId);

        if (recipe.getCommands() == null && recipe.getCommands().isEmpty()) {
            throw new NotFoundCustomException("Recipe does not contain any commands!");
        }
        if (index > 0 || index > recipe.getCommands().size() - 1) {
            throw new IndexOutOfBoundsException("Index of command is out of bounds!");
        }

        if (recipe.getCommands().get(index).getId().equals(commandId)) {
            recipe.getCommands().remove(index);
            recipeRepository.save(recipe);
            return recipe;
        }

        List<String> commandIndexes = new ArrayList<>();
        int currentIndex = 0;
        for (Command command : recipe.getCommands()) {
            if (command.getId().equals(commandId)) {
                commandIndexes.add(String.valueOf(currentIndex));
            }
            currentIndex++;
        }

        if (commandIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipe with ID '" + commandId + "' !");
        }
        throw new NotFoundCustomException("Command not found on index: " + index + "!"
                + " Commands with this ID can be found on indexes: " + String.join(", ", commandIndexes));
    }

    private boolean recipeWithNameExists(String name) {
        return recipeRepository.getRecipeByNameAndDeactivated(name, false) != null;
    }
}
