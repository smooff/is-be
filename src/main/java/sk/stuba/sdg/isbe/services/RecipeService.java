package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe createRecipe(Recipe recipe);

    Recipe getRecipeById(String recipeId);

    Recipe addCommandToRecipe(String recipeId, String commandId);

    Recipe removeCommandFromRecipe(String recipeId, String commandId, int index);

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);

    List<Recipe> getRecipesByTypeOfDevicePageable(String typeOfDevice, int page, int pageSize, String sortBy, String sortDirection);

    Recipe getRecipeByName(String name);

    Recipe updateRecipe(String recipeId, Recipe changeRecipe);

    Recipe addSubRecipeToRecipe(String recipeId, String subRecipeId);

    Recipe removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index);

    Recipe deleteRecipe(String recipeId);

    List<Recipe> getAllRecipes();

    List<Recipe> getAllRecipesPageable(int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getFullRecipes(String typeOfDevice);

    List<Recipe> getSubRecipes(String typeOfDevice);

    List<Recipe> getFullRecipesPageable(String typeOfDevice, int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getSubRecipesPageable(String typeOfDevice, int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getRecipesContainingCommand(Command command);
}
