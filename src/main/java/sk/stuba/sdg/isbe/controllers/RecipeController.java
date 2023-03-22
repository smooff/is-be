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

    @DeleteMapping("delete/{recipeId}")
    public void deleteRecipe(@PathVariable String recipeId) {
        recipeService.deleteRecipe(recipeId);
    }

    @GetMapping("getByTypeOfDevice/{type}")
    public List<Recipe> getRecipesByTypeOfDevice(@PathVariable String type) {
        return recipeService.getRecipesByTypeOfDevice(type);
    }

    @GetMapping("getByName/{name}")
    public Recipe getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @PutMapping("updateRecipe/{recipeId}/{changeRecipe}")
    public void updateRecipe(@PathVariable String recipeId, @PathVariable Recipe changeRecipe) {
        recipeService.updateRecipe(recipeId, changeRecipe);
    }

    @PutMapping("addSubRecipeToRecipe/{recipeId}/{subRecipeId}")
    public void addSubRecipeToRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId) {
        recipeService.addSubRecipeToRecipe(recipeId, subRecipeId);
    }

    @GetMapping("getFullRecipesByDeviceType/{typeOfDevice}")
    public List<Recipe> getFullRecipes(@PathVariable String typeOfDevice) {
        return recipeService.getFullRecipes(typeOfDevice);
    }

    @GetMapping("getSubRecipesByDeviceType/{typeOfDevice}")
    public List<Recipe> getSubRecipes(@PathVariable String typeOfDevice) {
        return recipeService.getSubRecipes(typeOfDevice);
    }
}
