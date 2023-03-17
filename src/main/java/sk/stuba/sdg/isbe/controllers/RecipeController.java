package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/recipe")
public class RecipeController {

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
