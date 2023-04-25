package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @PostMapping("createRecipe")
    public Recipe createRecipe(@Valid @RequestBody Recipe recipe) {
         return recipeService.createRecipe(recipe);
    }

    @Operation(summary = "Delete recipe by ID")
    @DeleteMapping("deleteRecipeById/{recipeId}")
    public Recipe deleteRecipe(@PathVariable String recipeId) {
        return recipeService.deleteRecipe(recipeId);
    }

    @Operation(summary = "Get recipe by name")
    @GetMapping("getRecipeByName/{name}")
    public Recipe getRecipeByName(@PathVariable String name) {
        return recipeService.getRecipeByName(name);
    }

    @Operation(summary = "Get recipe by id")
    @GetMapping("getRecipeById/{id}")
    public Recipe getRecipeById(@PathVariable String id) {
        return recipeService.getRecipeById(id);
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

    @Operation(summary = "Get recipes by device-type optionally with sorting")
    @GetMapping("getByDeviceType/{type}/{sortBy}/{sortDirection}")
    public List<Recipe> getRecipesByDeviceType(@PathVariable String type,
                                                 @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                 @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getRecipesByDeviceType(type, sortBy, sortDirection);
    }

    @Operation(summary = "Get recipes by device-type and pages optionally with sorting")
    @GetMapping("getByDeviceTypeAndPages/{type}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getRecipesByDeviceTypePageable(@PathVariable String type, @PathVariable int page, @PathVariable int pageSize,
                                                         @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                                         @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getRecipesByDeviceTypePageable(type, page, pageSize, sortBy, sortDirection);
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

    @Operation(summary = "Get all recipes optionally with sorting")
    @GetMapping("getAllRecipes/{sortBy}/{sortDirection}")
    public List<Recipe> getAllRecipes(@PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                      @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getAllRecipes(sortBy, sortDirection);
    }

    @Operation(summary = "Get all recipes with pagination optionally with sorting")
    @GetMapping("getAllRecipesPageable/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getAllRecipesPageable(@PathVariable int page, @PathVariable int pageSize,
                                              @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                              @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getAllRecipesPageable(page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get non-sub-recipes by device-type optionally with sorting")
    @GetMapping("getFullRecipesByDeviceType/{deviceType}/{sortBy}/{sortDirection}")
    public List<Recipe> getFullRecipes(@PathVariable String deviceType,
                                       @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                       @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getFullRecipes(deviceType, sortBy, sortDirection);
    }

    @Operation(summary = "Get non-sub-recipes by device-type and pages optionally with sorting")
    @GetMapping("getFullRecipesByDeviceTypeAndPages/{deviceType}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getFullRecipesPageable(@PathVariable String deviceType, @PathVariable int page, @PathVariable int pageSize,
                                               @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                               @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getFullRecipesPageable(deviceType, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get sub-recipes by device-type optionally with sorting")
    @GetMapping("getSubRecipesByDeviceType/{deviceType}/{sortBy}/{sortDirection}")
    public List<Recipe> getSubRecipes(@PathVariable String deviceType,
                                      @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                      @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getSubRecipes(deviceType, sortBy, sortDirection);
    }

    @Operation(summary = "Get sub-recipes by device-type and pages with sorting optionally with sorting")
    @GetMapping("getSubRecipesByDeviceType/{deviceType}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Recipe> getSubRecipesPageable(@PathVariable String deviceType, @PathVariable int page, @PathVariable int pageSize,
                                              @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortBy,
                                              @PathVariable @Parameter(description = "Unsorted -> NONE or NULL") String sortDirection) {
        return recipeService.getSubRecipesPageable(deviceType, page, pageSize, sortBy, sortDirection);
    }
}
