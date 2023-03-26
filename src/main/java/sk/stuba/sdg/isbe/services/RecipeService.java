package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe createRecipe(Recipe recipe);

    Recipe getRecipe(String recipeId);

    Recipe addCommandToRecipe(String recipeId, String commandId);

    Recipe removeCommandFromRecipe(String recipeId, String commandId, int index);

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);

    Recipe getRecipeByName(String name);

    ResponseEntity<Recipe> updateRecipe(String recipeId, Recipe changeRecipe);

    ResponseEntity<Recipe> addSubRecipeToRecipe(String recipeId, String subRecipeId);

    ResponseEntity<Recipe> removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index);

    ResponseEntity<Recipe> deleteRecipe(String recipeId);

    List<Recipe> getAllRecipes();

    List<Recipe> getFullRecipes(String typeOfDevice);

    List<Recipe> getSubRecipes(String typeOfDevice);

    List<Recipe> getRecipesContainingCommand(Command command);
}
