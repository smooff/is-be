package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public void createRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        return recipeRepository.getRecipesByTypeOfDevice(typeOfDevice);
    }
}
