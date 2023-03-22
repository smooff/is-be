package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidRecipeException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.handlers.exceptions.RecipeExistsException;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public Recipe createRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().equals(EMPTY_STRING)) {
            throw new InvalidRecipeException("Recipe has no name set!");
        }

        if (recipeNameExists(recipe.getName())) {
            throw new RecipeExistsException("Recipe with name: '" + recipe.getName() + "' already exists!");
        }

        if (recipe.getTypeOfDevice() == null || recipe.getTypeOfDevice().equals(EMPTY_STRING)) {
            throw new InvalidRecipeException("Type of device for recipe is missing!");
        }

        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        return recipeRepository.getRecipesByTypeOfDevice(typeOfDevice);
    }

    @Override
    public Recipe getRecipeByName(String name) {
        Recipe foundRecipe = recipeRepository.getRecipeByName(name);
        if (foundRecipe == null) {
            throw new NotFoundCustomException("Recipe with name: '" + name + "' does not exist!");
        }
        return foundRecipe;
    }

    @Override
    public ResponseEntity<Recipe> updateRecipe(String recipeId, Recipe changeRecipe) {
        Recipe recipe = getRecipe(recipeId);

        if (!recipe.getName().equals(changeRecipe.getName()) && recipeNameExists(changeRecipe.getName())) {
            throw new RecipeExistsException("Recipe with name: '" + changeRecipe.getName() + "' already exists!");
        }

        if (changeRecipe.getName() != null) {
            recipe.setName(changeRecipe.getName());
        }
        if (changeRecipe.getCommands() != null) {
            recipe.setCommands(changeRecipe.getCommands());
        }
        if (changeRecipe.getTypeOfDevice() != null) {
            recipe.setTypeOfDevice(changeRecipe.getTypeOfDevice());
        }
        if (changeRecipe.isSubRecipe() != null) {
            recipe.setSubRecipe(changeRecipe.isSubRecipe());
        }

        recipeRepository.save(recipe);
        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> addSubRecipeToRecipe(String recipeId, String subRecipeId) {
        Recipe recipe = getRecipe(recipeId);
        Recipe subRecipe = getRecipe(subRecipeId);

        if (recipe.isSubRecipe()) {
            throw new InvalidRecipeException("Provided recipe is a sub-recipe!");
        }
        if (!subRecipe.isSubRecipe()) {
            throw new InvalidRecipeException("Provided sub-recipe is a proper recipe!");
        }
        if (!recipe.getTypeOfDevice().equals(subRecipe.getTypeOfDevice())) {
            throw new InvalidRecipeException("Device type of the recipes does not match!");
        }

        recipe.getCommands().addAll(subRecipe.getCommands());

        return ResponseEntity.ok(recipe);
    }

    @Override
    public ResponseEntity<Recipe> deleteRecipe(String recipeId) {
        Recipe recipe = getRecipe(recipeId);
        recipeRepository.delete(recipe);
        return ResponseEntity.ok(recipe);
    }

    @Override
    public List<Recipe> getFullRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDevice(false, typeOfDevice);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDevice(true, typeOfDevice);
    }

    @Override
    public Recipe getRecipe(String recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.findById(recipeId);
        if (optionalRecipe.isEmpty()) {
            throw new NotFoundCustomException("Recipe with ID: " + recipeId + " was not found!");
        }
        return optionalRecipe.get();
    }

    private boolean recipeNameExists(String name) {
        return getRecipeByName(name) != null;
    }
}
