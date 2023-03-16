package sk.stuba.sdg.isbe.services.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.entities.job.Recipe;
import sk.stuba.sdg.isbe.services.repository.job.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    public void createRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        return recipeRepository.getRecipesByTypeOfDevice(typeOfDevice);
    }
}
