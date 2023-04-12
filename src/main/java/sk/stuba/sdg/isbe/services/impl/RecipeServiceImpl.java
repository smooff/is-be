package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.enums.SortDirectionEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.RecipeService;
import sk.stuba.sdg.isbe.utilities.DeviceTypeUtils;
import sk.stuba.sdg.isbe.utilities.SortingUtils;

import java.lang.reflect.Field;
import java.util.*;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CommandService commandService;

    @Override
    public Recipe createRecipe(Recipe recipe) {
        if (recipe.getName() == null || recipe.getName().isEmpty()) {
            throw new InvalidEntityException("Recipe has no name set!");
        }
        if (recipeWithNameExists(recipe.getName())) {
            throw new InvalidEntityException("Recipe with name: '" + recipe.getName() + "' already exists!");
        }
        if (recipe.getTypeOfDevice() == null) {
            throw new InvalidEntityException("Type of device for recipe is missing!");
        }
        if (recipe.isSubRecipe() == null) {
            throw new InvalidEntityException("Recipe's sub-recipe status not defined!");
        }

        return recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice) {
        DeviceTypeEnum deviceType = DeviceTypeUtils.getDeviceTypeEnum(typeOfDevice);
        List<Recipe> recipes = recipeRepository.getRecipesByTypeOfDeviceAndDeactivated(deviceType, false);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes with type of Device: " + typeOfDevice + " in the database!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getRecipesByTypeOfDevicePageable(String typeOfDevice, int page, int pageSize, String sortBy, String sortDirection) {
        if (page <= 0) {
            throw new InvalidOperationException("Page must be greater than 0!");
        }
        if (pageSize <= 0) {
            throw new InvalidOperationException("Size of the page must be greater than 0!");
        }

        DeviceTypeEnum deviceType = DeviceTypeUtils.getDeviceTypeEnum(typeOfDevice);
        Sort sorting = getDirectedSorting(Sort.by(getValidRecipeSortingField(sortBy)), sortDirection);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sorting);
        List<Recipe> recipes = recipeRepository.getRecipesByTypeOfDeviceAndDeactivated(deviceType, false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes with type of Device: " + typeOfDevice + " on this page!");
        }
        return recipes;
    }

    @Override
    public Recipe getRecipeByName(String name) {
        Optional<Recipe> recipe = recipeRepository.getRecipeByNameAndDeactivated(name, false);
        if (recipe.isEmpty()) {
            throw new NotFoundCustomException("Recipe with name: '" + name + "' does not exist!");
        }
        return recipe.get();
    }

    @Override
    public Recipe updateRecipe(String recipeId, Recipe changeRecipe) {
        Recipe recipe = getRecipeById(recipeId);

        if (changeRecipe == null) {
            throw new InvalidEntityException("Recipe with changes is null!");
        }

        if (!recipe.getName().equals(changeRecipe.getName()) && recipeWithNameExists(changeRecipe.getName())) {
            throw new EntityExistsException("Recipe with name: '" + changeRecipe.getName() + "' already exists!");
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
        if (changeRecipe.getSubRecipes() != null) {
            recipe.setSubRecipes(changeRecipe.getSubRecipes());
        }

        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe addSubRecipeToRecipe(String recipeId, String subRecipeId) {
        if (Objects.equals(recipeId, subRecipeId)) {
            throw new InvalidOperationException("Recipe can't be added as its own sub-recipe to prevent infinite loop!");
        }

        Recipe recipe = getRecipeById(recipeId);
        Recipe subRecipe = getRecipeById(subRecipeId);

        if (recipe.getTypeOfDevice() != subRecipe.getTypeOfDevice()) {
            throw new InvalidEntityException("Device types of the recipes do not match!"
            + "\nRecipe's device type: " + recipe.getTypeOfDevice()
            + "\nSub-recipe's device type: " + subRecipe.getTypeOfDevice());
        }

        if (subRecipe.getSubRecipes() != null) {
            List<String> subRecipeIds = subRecipe.getSubRecipes().stream().map(Recipe::getId).toList();
            if (subRecipeIds.contains(recipeId)) {
                throw new InvalidOperationException("The list of sub-recipes of the given sub-recipe contains the recipe," +
                        " therefore it can't be used as sub-recipe of the recipe to prevent infinite loop.");
            }
        }

        if (recipe.getSubRecipes() == null) {
            recipe.setSubRecipes(new ArrayList<>());
        }

        recipe.getSubRecipes().add(subRecipe);
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe removeSubRecipeFromRecipe(String recipeId, String subRecipeId, int index) {
        Recipe recipe = getRecipeById(recipeId);
        if (subRecipeId == null) {
            throw new NullPointerException("ID of sub-recipe must not be null!");
        }

        if (recipe.getSubRecipes() == null || recipe.getSubRecipes().isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipes!");
        }

        if (index < 0 || index > recipe.getSubRecipes().size() - 1) {
            throw new IndexOutOfBoundsException("Provided index of subRecipe is not valid!");
        }

        if (recipe.getSubRecipes().get(index).getId().equals(subRecipeId)) {
            recipe.getSubRecipes().remove(index);
            return recipeRepository.save(recipe);
        }

        List<String> subRecipeIndexes = new ArrayList<>();
        int currentIndex = 0;
        for (Recipe subRecipeOfRecipe : recipe.getSubRecipes()) {
            if (subRecipeOfRecipe.getId().equals(subRecipeId)) {
                subRecipeIndexes.add(String.valueOf(currentIndex));
            }
            currentIndex++;
        }
        if (subRecipeIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipe with ID '" + subRecipeId + "'!");
        }

        throw new NotFoundCustomException("Sub-recipe not found on index: " + index + "!"
                                          + "\nSub-recipes with this ID can be found on indexes: " + String.join(", ", subRecipeIndexes) + ".");
    }

    @Override
    public Recipe deleteRecipe(String recipeId) {
        Recipe recipeToDelete = getRecipeById(recipeId);
        List<Recipe> recipesUsingThis = recipeRepository.getRecipesBySubRecipesContaining(recipeToDelete);
        List<String> recipeNames = recipesUsingThis.stream().map(Recipe::getName).toList();

        if (!recipesUsingThis.isEmpty()) {
            throw new InvalidOperationException("Recipe is used in Recipes as sub-recipe: \n" + String.join("\n", recipeNames)
                    + "\nRemove recipe from all recipes to be able to delete it!");
        }

        recipeToDelete.setDeactivated(true);
        return recipeRepository.save(recipeToDelete);
    }

    @Override
    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAll();
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes in the database yet!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getFullRecipes(String typeOfDevice) {
        return getFullOrSubRecipes(typeOfDevice, false);
    }

    @Override
    public List<Recipe> getSubRecipes(String typeOfDevice) {
        return getFullOrSubRecipes(typeOfDevice, true);
    }

    private List<Recipe> getFullOrSubRecipes(String deviceType, boolean isSubRecipe) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        List<Recipe> recipes = recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(isSubRecipe, deviceTypeEnum, false);
        if (recipes.isEmpty()) {
            String recipeType = isSubRecipe ? "sub-recipes" : "recipes";
            throw new NotFoundCustomException("There are not any " + recipeType + " in the database yet!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getFullRecipesPageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection) {
        return getFullOrSubRecipesPageable(deviceType, false, page, pageSize, sortBy, sortDirection);
    }

    @Override
    public List<Recipe> getSubRecipesPageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection) {
        return getFullOrSubRecipesPageable(deviceType, true, page, pageSize, sortBy, sortDirection);
    }

    private List<Recipe> getFullOrSubRecipesPageable(String deviceType, boolean isSubRecipe, int page, int pageSize, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        Sort sorting = getDirectedSorting(Sort.by(getValidRecipeSortingField(sortBy)), sortDirection);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sorting);
        List<Recipe> recipes = recipeRepository.getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(isSubRecipe, deviceTypeEnum, false, pageable);
        if (recipes.isEmpty()) {
            String recipeType = isSubRecipe ? "sub-recipes" : "recipes";
            throw new NotFoundCustomException("There are not any " + recipeType + " with type of Device: " + deviceType + " on this page!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getRecipesContainingCommand(Command command) {
        return recipeRepository.getRecipesByCommandsContaining(command);
    }

    @Override
    public Recipe getRecipeById(String recipeId) {
        Optional<Recipe> optionalRecipe = recipeRepository.getRecipeByIdAndDeactivated(recipeId, false);
        if (optionalRecipe.isEmpty()) {
            throw new NotFoundCustomException("Recipe with ID: '" + recipeId + "' was not found!");
        }
        return optionalRecipe.get();
    }

    @Override
    public Recipe addCommandToRecipe(String recipeId, String commandId) {
        Recipe recipe = getRecipeById(recipeId);
        Command command = commandService.getCommandById(commandId);

        if (command.isDeactivated()) {
            throw new InvalidOperationException("Command can't be found!");
        }
        if (command.getTypeOfDevice() != recipe.getTypeOfDevice()) {
            throw new InvalidOperationException("Types of devices of the command and recipe do not match!"
            + "\nRecipes device type: " + recipe.getTypeOfDevice()
            + "\nCommands device type: " + command.getTypeOfDevice());
        }
        if (command.getName() == null || command.getName().isEmpty()) {
            throw new InvalidOperationException("Command does not have any name set!");
        }
        if (command.getParams() == null || command.getParams().isEmpty()) {
            throw new InvalidOperationException("Command does not have any parameters!");
        }

        if (recipe.getCommands() == null) {
            recipe.setCommands(new ArrayList<>());
        }

        recipe.getCommands().add(command);
        return recipeRepository.save(recipe);
    }

    @Override
    public Recipe removeCommandFromRecipe(String recipeId, String commandId, int index) {
        Recipe recipe = getRecipeById(recipeId);

        if (recipe.getCommands() == null || recipe.getCommands().isEmpty()) {
            throw new NotFoundCustomException("Recipe does not contain any commands!");
        }
        if (index < 0 || index > recipe.getCommands().size() - 1) {
            throw new IndexOutOfBoundsException("Index of command is out of bounds!");
        }

        if (recipe.getCommands().get(index).getId().equals(commandId)) {
            recipe.getCommands().remove(index);
            return recipeRepository.save(recipe);
        }

        List<String> commandIndexes = new ArrayList<>();
        int currentIndex = 0;
        for (Command command : recipe.getCommands()) {
            if (command.getId().equals(commandId)) {
                commandIndexes.add(String.valueOf(currentIndex));
            }
            currentIndex++;
        }

        if (commandIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any command with ID '" + commandId + "'!");
        }
        throw new NotFoundCustomException("Command not found on index: " + index + "!"
                + "\nCommands with this ID can be found on indexes: " + String.join(", ", commandIndexes));
    }

    private boolean recipeWithNameExists(String name) {
        return recipeRepository.getRecipeByNameAndDeactivated(name, false).isPresent();
    }

    private Sort getDirectedSorting(Sort sorting, String sortDirection) {
        SortDirectionEnum sortDirectionEnum = SortingUtils.getSortDirection(sortDirection);
        if (SortDirectionEnum.NONE == sortDirectionEnum) {
            return sorting;
        }
        return SortDirectionEnum.ASC == sortDirectionEnum ? sorting.ascending() : sorting.descending();
    }

    private String getValidRecipeSortingField(String sortBy) {
        List<String> recipeSortingFields = Arrays.stream(Recipe.class.getDeclaredFields()).map(Field::getName).toList();
        for (String field : recipeSortingFields) {
            if (field.equalsIgnoreCase(sortBy)) {
                return field;
            }
        }
        throw new NotFoundCustomException("Sorting field: " + sortBy + " can't be found in Recipe class!" +
                "Possible sorting fields: " + String.join(", ", recipeSortingFields));
    }
}
