package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Create a recipe in database using object")
    @PostMapping("create")
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
         return recipeService.createRecipe(recipe);
    }

    @Operation(summary = "Delete recipe by ID")
    @DeleteMapping("delete/{recipeId}")
    public ResponseEntity<Recipe> deleteRecipe(@PathVariable String recipeId) {
        return recipeService.deleteRecipe(recipeId);
    }

    @Operation(summary = "Assign a command to a recipe by their ID")
    @PutMapping("addCommandToRecipe/{recipeId}/{commandId}")
    public Recipe addCommandToRecipe(@PathVariable String recipeId, @PathVariable String commandId) {
        return recipeService.addCommandToRecipe(recipeId, commandId);
    }

    @Operation(summary = "Remove command from recipe by their ID and index of the command")
    @DeleteMapping("removeCommandFromRecipe/{recipeId}/{commandId}/{index}")
    public Recipe removeCommandFromRecipe(@PathVariable String recipeId, @PathVariable String commandId, @PathVariable int index) {
        return recipeService.removeCommandFromRecipe(recipeId, commandId, index);
    }

    @Operation(summary = "Get recipes by device-type")
    @GetMapping("getByTypeOfDevice/{type}")
    public List<Recipe> getRecipesByTypeOfDevice(@PathVariable String type) {
        return recipeService.getRecipesByTypeOfDevice(type);
    }

    @Operation(summary = "Get recipe by name")
    @GetMapping("getByName/{name}")
    public Recipe getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @Operation(summary = "Update recipe by ID using object")
    @PutMapping("updateRecipe/{recipeId}")
    public void updateRecipe(@PathVariable String recipeId, @Valid @RequestBody Recipe changeRecipe) {
        recipeService.updateRecipe(recipeId, changeRecipe);
    }

    @Operation(summary = "Add sub-recipe to a recipe using their IDs")
    @PutMapping("addSubRecipeToRecipe/{recipeId}/{subRecipeId}")
    public void addSubRecipeToRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId) {
        recipeService.addSubRecipeToRecipe(recipeId, subRecipeId);
    }

    @Operation(summary = "Remove a recipe from sub-recipe using their IDs and index of the sub-recipe")
    @DeleteMapping("removeSubRecipeFromRecipe/{recipeId}/{subRecipeId}/{index}")
    public void removeSubRecipeFromRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId, @PathVariable int index) {
        recipeService.removeSubRecipeFromRecipe(recipeId, subRecipeId, index);
    }

    @Operation(summary = "Get all existing recipes")
    @GetMapping("getAllRecipes")
    public List<Recipe> getAllRecipes() {
        return recipeService.getAllRecipes();
    }

    @Operation(summary = "Get non-sub-recipes by device-type")
    @GetMapping("getFullRecipesByDeviceType/{typeOfDevice}")
    public List<Recipe> getFullRecipes(@PathVariable String typeOfDevice) {
        return recipeService.getFullRecipes(typeOfDevice);
    }

    @Operation(summary = "Get sub-recipes by device-type")
    @GetMapping("getSubRecipesByDeviceType/{typeOfDevice}")
    public List<Recipe> getSubRecipes(@PathVariable String typeOfDevice) {
        return recipeService.getSubRecipes(typeOfDevice);
    }
}
