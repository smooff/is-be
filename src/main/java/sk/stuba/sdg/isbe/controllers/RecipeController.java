package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    public Recipe createRecipe(@PathVariable Recipe recipe) {
         return recipeService.createRecipe(recipe);
    }

    @DeleteMapping("delete/{recipeId}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable String recipeId) {
        return recipeService.deleteRecipe(recipeId);
    }

    @PutMapping("addCommandToRecipe/{recipeId}/{commandId}")
    public Recipe addCommandToRecipe(@PathVariable String recipeId, @PathVariable String commandId) {
        return recipeService.addCommandToRecipe(recipeId, commandId);
    }

    @DeleteMapping("removeCommandFromRecipe/{recipeId}/{commandId}")
    public Recipe removeCommandFromRecipe(@PathVariable String recipeId, @PathVariable String commandId) {
        return recipeService.removeCommandFromRecipe(recipeId, commandId);
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

    @DeleteMapping("removeSubRecipeFromRecipe/{recipeId}/{subRecipeId}/{index}")
    public void removeSubRecipeFromRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId, @PathVariable int index) {
        recipeService.removeSubRecipeFromRecipe(recipeId, subRecipeId, index);
    }

    @GetMapping("getAllRecipes")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
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
