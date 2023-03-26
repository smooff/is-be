package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
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
        return recipeRepository.getRecipesByTypeOfDeviceAndDeactivated(typeOfDevice.toUpperCase(), false);
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

        if (!recipe.getName().equals(changeRecipe.getName()) && recipeWithNameExists(changeRecipe.getName())) {
            throw new EntityExistsException("Recipe with name: '" + changeRecipe.getName() + "' already exists!");
        }

        if (changeRecipe.getName() != null) {
            recipe.setName(changeRecipe.getName());
        }
        if (changeRecipe.getCommandIds() != null) {
            recipe.setCommandIds(changeRecipe.getCommandIds());
        }
        if (changeRecipe.getTypeOfDevice() != null) {
            recipe.setTypeOfDevice(changeRecipe.getTypeOfDevice());
        }
        if (changeRecipe.isSubRecipe() != null) {
            recipe.setSubRecipe(changeRecipe.isSubRecipe());
        }
        if (changeRecipe.getSubRecipeIds() != null) {
            recipe.setSubRecipeIds(changeRecipe.getSubRecipeIds());
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
        if (recipe.getSubRecipeIds() == null) {
            recipe.setSubRecipeIds(new ArrayList<>());
        }

        recipe.getSubRecipeIds().add(subRecipe.getId());
        recipeRepository.save(recipe);

        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index) {
        Recipe recipe = getRecipe(recipeId);
        if (subRecipeId == null) {
            throw new NullPointerException("ID of sub-recipe must not be null!");
        }

        if (recipe.getSubRecipeIds() == null || recipe.getSubRecipeIds().isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipes!");
        }

        if (index < 0 || index > recipe.getSubRecipeIds().size() - 1) {
            throw new IndexOutOfBoundsException("Provided index of subRecipe is not valid!");
        }

        if (recipe.getSubRecipeIds().get(index).equals(subRecipeId)) {
            recipe.getSubRecipeIds().remove(index);
            recipeRepository.save(recipe);
            return ResponseEntity.ok(recipe);
        }

        List<String> subRecipeIndexes = new ArrayList<>();
        int currentIndex = 0;
        for (String subRecipeOfRecipeId : recipe.getSubRecipeIds()) {
            if (subRecipeOfRecipeId.equals(subRecipeId)) {
                subRecipeIndexes.add(String.valueOf(currentIndex));
            }
            currentIndex++;
        }
        if (subRecipeIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipe with ID '" + subRecipeId + "' !");
        }

        throw new NotFoundCustomException("Sub-recipe not found on index: " + index + "!"
                                          + "Sub-recipes with this ID can be found on indexes: " + String.join(", ", subRecipeIndexes));
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
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(false, typeOfDevice.toUpperCase(), false);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(true, typeOfDevice.toUpperCase(), false);
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

        if (recipe.getCommandIds() == null) {
            recipe.setCommandIds(List.of(command.getId()));
        } else {
            recipe.getCommandIds().add(command.getId());
        }
        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public Recipe removeCommandFromRecipe(String recipeId, String commandId) {
        Recipe recipe = getRecipe(recipeId);

        if (recipe.getCommandIds() == null && recipe.getCommandIds().isEmpty()) {
            throw new NotFoundCustomException("Recipe does not contain any commands!");
        }

        List<String> commandIds = new ArrayList<>();

        for (String commandIdInRecipe : recipe.getCommandIds()) {
            if (commandIdInRecipe.equals(commandId)) {
                commandIds.add(commandId);
            }
        }

        recipe.getCommandIds().removeAll(commandIds);
        recipeRepository.save(recipe);

        if (!commandIds.isEmpty()) {
            return recipe;
        }
        throw new NotFoundCustomException("Recipe does not contain command with ID: " + commandId);
    }

    private boolean recipeWithNameExists(String name) {
        return recipeRepository.getRecipeByNameAndDeactivated(name, false) != null;
    }
}
