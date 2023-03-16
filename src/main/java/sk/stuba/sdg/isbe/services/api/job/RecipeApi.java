package sk.stuba.sdg.isbe.services.api.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.entities.job.Recipe;
import sk.stuba.sdg.isbe.services.service.job.RecipeService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/recipe")
public class RecipeApi {

    @Autowired
    private RecipeService recipeService;

    @PostMapping("create/{recipe}")
    public void createRecipe(@PathVariable Recipe recipe) {
        recipeService.createRecipe(recipe);
    }

    @GetMapping("getByTypeOfDevice/{type}")
    public List<Recipe> getRecipesByTypeOfDevice(@PathVariable String type) {
        return recipeService.getRecipesByTypeOfDevice(type);
    }
}
