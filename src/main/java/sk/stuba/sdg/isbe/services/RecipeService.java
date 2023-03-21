package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface RecipeService {

    Recipe createRecipe(Recipe recipe);

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);

    Recipe getRecipeByName(String name);

    Recipe updateRecipe(Recipe recipe, Recipe recipeWithNewSettings);

    void deleteRecipe(Recipe recipe);

    List<Recipe> getFullRecipes(String typeOfDevice);

    List<Recipe> getSubRecipes(String typeOfDevice);
}
