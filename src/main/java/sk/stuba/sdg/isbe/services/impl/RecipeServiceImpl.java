package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidRecipeException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.handlers.exceptions.RecipeExistsException;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.List;

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

        if (recipeExists(recipe.getName())) {
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
    public Recipe updateRecipe(Recipe recipe, Recipe recipeWithNewSettings) {
        if (recipeWithNewSettings == null || recipe == null) {
            throw new InvalidRecipeException("Recipe is null!");
        }

        if (!recipeExists(recipe.getName())) {
            throw new NotFoundCustomException("Recipe with name: '" + recipe.getName() + "' does not exist!");
        }

        if (!recipe.getName().equals(recipeWithNewSettings.getName()) && recipeExists(recipeWithNewSettings.getName())) {
            throw new RecipeExistsException("Recipe with name: '" + recipeWithNewSettings.getName() + "' already exists!");
        }

        if (recipeWithNewSettings.getName() != null) {
            recipe.setName(recipeWithNewSettings.getName());
        }
        if (recipeWithNewSettings.getCommands() != null) {
            recipe.setCommands(recipeWithNewSettings.getCommands());
        }
        if (recipeWithNewSettings.getTypeOfDevice() != null) {
            recipe.setTypeOfDevice(recipeWithNewSettings.getTypeOfDevice());
        }
        if (recipeWithNewSettings.isSubRecipe() != null) {
            recipe.setSubRecipe(recipeWithNewSettings.isSubRecipe());
        }

        recipeRepository.save(recipe);
        return recipe;
    }

    @Override
    public void deleteRecipe(Recipe recipe) {
        recipeRepository.delete(recipe);
    }

    @Override
    public List<Recipe> getFullRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDevice(false, typeOfDevice);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        return recipeRepository.getRecipesByIsSubRecipeAndTypeOfDevice(true, typeOfDevice);
    }

    private boolean recipeExists(String name) {
        Recipe foundRecipe = getRecipeByName(name);
        return foundRecipe != null;
    }
}
