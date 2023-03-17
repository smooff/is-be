package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface RecipeService {

    void createRecipe(Recipe recipe);

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);
}
