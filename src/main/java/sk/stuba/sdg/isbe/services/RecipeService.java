package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe createRecipe(Recipe recipe);

    Recipe getRecipeById(String recipeId);

    Recipe addCommandToRecipe(String recipeId, String commandId);

    Recipe removeCommandFromRecipe(String recipeId, String commandId, int index);

    List<Recipe> getRecipesByDeviceType(String deviceType, String sortBy, String sortDirection);

    List<Recipe> getRecipesByDeviceTypePageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection);

    Recipe getRecipeByName(String name);

    Recipe updateRecipe(String recipeId, Recipe changeRecipe);

    Recipe addSubRecipeToRecipe(String recipeId, String subRecipeId);

    Recipe removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index);

    Recipe deleteRecipe(String recipeId);

    List<Recipe> getAllRecipes(String sortBy, String sortDirection);

    List<Recipe> getAllRecipesPageable(int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getFullRecipes(String deviceType, String sortBy, String sortDirection);

    List<Recipe> getSubRecipes(String deviceType, String sortBy, String sortDirection);

    List<Recipe> getFullRecipesPageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getSubRecipesPageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection);

    List<Recipe> getRecipesContainingCommand(Command command);
}
