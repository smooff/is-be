package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @Operation(summary = "Create a recipe in database using object")
    @PostMapping("create")
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
         return recipeService.createRecipe(recipe);
    }

    @Operation(summary = "Delete recipe by ID")
    @DeleteMapping("delete/{recipeId}")
    public Recipe deleteRecipe(@PathVariable String recipeId) {
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
    @GetMapping("getByTypeOfDevice/{type}/{sortBy}/{sortDirection}")
    public List<Recipe> getRecipesByTypeOfDevice(@PathVariable String type, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getRecipesByTypeOfDevice(type, sortBy, sortDirection);
    }

    @Operation(summary = "Get recipes by device-type and pages with sorting")
    @GetMapping("getByTypeOfDeviceAndPages/{type}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getRecipesByTypeOfDevicePageable(@PathVariable String type, @PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getRecipesByTypeOfDevicePageable(type, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get recipe by name")
    @GetMapping("getByName/{name}")
    public Recipe getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @Operation(summary = "Update recipe by ID using object")
    @PutMapping("updateRecipe/{recipeId}")
    public Recipe updateRecipe(@PathVariable String recipeId, @Valid @RequestBody Recipe changeRecipe) {
        return recipeService.updateRecipe(recipeId, changeRecipe);
    }

    @Operation(summary = "Add sub-recipe to a recipe using their IDs")
    @PutMapping("addSubRecipeToRecipe/{recipeId}/{subRecipeId}")
    public Recipe addSubRecipeToRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId) {
        return recipeService.addSubRecipeToRecipe(recipeId, subRecipeId);
    }

    @Operation(summary = "Remove a recipe from sub-recipe using their IDs and index of the sub-recipe")
    @DeleteMapping("removeSubRecipeFromRecipe/{recipeId}/{subRecipeId}/{index}")
    public Recipe removeSubRecipeFromRecipe(@PathVariable String recipeId, @PathVariable String subRecipeId, @PathVariable int index) {
        return recipeService.removeSubRecipeFromRecipe(recipeId, subRecipeId, index);
    }

    @Operation(summary = "Get all recipes sorted by a field or non-sorted - NONE.")
    @GetMapping("getAllRecipes/{sortBy}/{sortDirection}")
    public List<Recipe> getAllRecipes(@PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getAllRecipes(sortBy, sortDirection);
    }

    @Operation(summary = "Get all recipes with pagination and sorting")
    @GetMapping("getAllRecipesPageable/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getAllRecipesPageable(@PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getAllRecipesPageable(page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get non-sub-recipes by device-type")
    @GetMapping("getFullRecipesByDeviceType/{typeOfDevice}/{sortBy}/{sortDirection}")
    public List<Recipe> getFullRecipes(@PathVariable String typeOfDevice, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getFullRecipes(typeOfDevice, sortBy, sortDirection);
    }

    @Operation(summary = "Get non-sub-recipes by device-type and pages with sorting")
    @GetMapping("getFullRecipesByDeviceTypeAndPages/{typeOfDevice}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getFullRecipesPageable(@PathVariable String typeOfDevice, @PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getFullRecipesPageable(typeOfDevice, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get sub-recipes by device-type")
    @GetMapping("getSubRecipesByDeviceType/{typeOfDevice}/{sortBy}/{sortDirection}")
    public List<Recipe> getSubRecipes(@PathVariable String typeOfDevice, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getSubRecipes(typeOfDevice, sortBy, sortDirection);
    }

    @Operation(summary = "Get sub-recipes by device-type and pages with sorting")
    @GetMapping("getSubRecipesByDeviceType/{typeOfDevice}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getSubRecipesPageable(@PathVariable String typeOfDevice, @PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return recipeService.getSubRecipesPageable(typeOfDevice, page, pageSize, sortBy, sortDirection);
    }
}
