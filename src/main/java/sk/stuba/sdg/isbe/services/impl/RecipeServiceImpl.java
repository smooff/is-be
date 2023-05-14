package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
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

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

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
        if (recipe.getDeviceType() == null) {
            throw new InvalidEntityException("Type of device for recipe is missing!");
        }
        if (recipe.isSubRecipe() == null) {
            throw new InvalidEntityException("Recipe's sub-recipe status not defined!");
        }

        recipe.setCreatedAt(Instant.now().toEpochMilli());

        return recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> getRecipesByDeviceType(String deviceType, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        List<Recipe> recipes = recipeRepository.getRecipesByDeviceTypeAndDeactivated(deviceTypeEnum, false, SortingUtils.getSort(Recipe.class, sortBy, sortDirection));
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes with type of Device: " + deviceType + " in the database!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getRecipesByDeviceTypePageable(String deviceType, int page, int pageSize, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        Pageable pageable = SortingUtils.getFirstEntry(Recipe.class);
        List<Recipe> recipes = recipeRepository.getRecipesByDeviceTypeAndDeactivated(deviceTypeEnum, false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipe of device type '" + deviceType + "' in the database!");
        }

        pageable = SortingUtils.getPagination(Recipe.class, sortBy, sortDirection, page, pageSize);
        recipes = recipeRepository.getRecipesByDeviceTypeAndDeactivated(deviceTypeEnum, false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes of type of device: " + deviceType + " on this page!");
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
        if (changeRecipe.getName() != null) {
            if (!recipe.getName().equals(changeRecipe.getName()) && recipeWithNameExists(changeRecipe.getName())) {
                throw new EntityExistsException("Recipe with name: '" + changeRecipe.getName() + "' already exists!");
            } else {
                recipe.setName(changeRecipe.getName());
            }
        }

        if (changeRecipe.isSubRecipe() != null) {
            recipe.setSubRecipe(changeRecipe.isSubRecipe());
        }

        if (changeRecipe.getDeviceType() != null && changeRecipe.getDeviceType() != recipe.getDeviceType()) {
            if (recipe.getCommands() != null && !recipe.getCommands().isEmpty()) {
                throw new InvalidOperationException("Device type of recipe can't be changed, since the recipe contains commands!");
            }
            if (recipe.getSubRecipes() != null && !recipe.getSubRecipes().isEmpty()) {
                throw new InvalidOperationException("Device type of recipe can't be changed, since the recipe contains sub-recipes!");
            }
            recipe.setDeviceType(changeRecipe.getDeviceType());
        }

        //save local changes that were made above, further changes are made on database level
        recipeRepository.save(recipe);

        if (changeRecipe.getCommands() != null) {
            recipe = removeAllCommandsFromRecipe(recipe);
            recipe = addAllCommandsToRecipe(recipe, changeRecipe.getCommands());
        }

        if (changeRecipe.getSubRecipes() != null) {
            recipe = removeAllSubRecipesFromRecipe(recipe);
            recipe = addAllSubRecipesToRecipe(recipe, changeRecipe.getSubRecipes());
        }

        return recipe;
    }

    private Recipe addAllCommandsToRecipe(Recipe recipe, List<Command> commands) {
        for (Command command : commands) {
            recipe = addCommandToRecipe(recipe.getId(), command.getId());
        }
        return recipe;
    }

    private Recipe addAllSubRecipesToRecipe(Recipe recipe, List<Recipe> recipes) {
        for (Recipe subRecipe : recipes) {
            recipe = addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());
        }
        return recipe;
    }

    private Recipe removeAllCommandsFromRecipe(Recipe recipe) {
        if (recipe.getCommands() != null && !recipe.getCommands().isEmpty()) {
            int lastIndex = recipe.getCommands().size() - 1;
            for (int i = lastIndex; i >= 0; i--) {
                recipe = removeCommandFromRecipe(recipe.getId(), recipe.getCommands().get(i).getId(), i);
            }
        }
        return recipe;
    }

    private Recipe removeAllSubRecipesFromRecipe(Recipe recipe) {
        if (recipe.getSubRecipes() != null && !recipe.getSubRecipes().isEmpty()) {
            int lastIndex = recipe.getSubRecipes().size() - 1;
            for (int i = lastIndex; i >= 0; i--) {
                recipe = removeSubRecipeFromRecipe(recipe.getId(), recipe.getSubRecipes().get(i).getId(), i);
            }
        }
        return recipe;
    }

    @Override
    public Recipe addSubRecipeToRecipe(String recipeId, String subRecipeId) {
        if (Objects.equals(recipeId, subRecipeId)) {
            throw new InvalidOperationException("Recipe can't be added as its own sub-recipe to prevent infinite loop!");
        }

        Recipe recipe = getRecipeById(recipeId);
        Recipe subRecipe = getRecipeById(subRecipeId);

        if (recipe.getDeviceType() != subRecipe.getDeviceType()) {
            throw new InvalidEntityException("Device types of the recipes do not match!"
            + " Recipe's device type: " + recipe.getDeviceType()
            + ", Sub-recipe's device type: " + subRecipe.getDeviceType());
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
        Recipe subRecipe = getRecipeById(subRecipeId);

        if (recipe.getSubRecipes() == null || recipe.getSubRecipes().isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipes!");
        }
        if (index < 0 || index > recipe.getSubRecipes().size() - 1) {
            throw new NotFoundCustomException("Provided index of subRecipe is not valid!");
        }
        if (recipe.getSubRecipes().get(index).getId().equals(subRecipe.getId())) {
            recipe.getSubRecipes().remove(index);
            return recipeRepository.save(recipe);
        }

        List<String> subRecipeIndexes = IntStream.range(0, recipe.getSubRecipes().size())
                .filter(i -> recipe.getSubRecipes().get(i).getId().equals(subRecipeId))
                .mapToObj(String::valueOf)
                .toList();
        if (subRecipeIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any sub-recipe with ID '" + subRecipeId + "'!");
        }

        throw new NotFoundCustomException("Sub-recipe not found on index: " + index + "!"
                + " Sub-recipes with this ID can be found on indexes: " + String.join(", ", subRecipeIndexes) + ".");
    }

    @Override
    public Recipe deleteRecipe(String recipeId) {
        Recipe recipeToDelete = getRecipeById(recipeId);
        List<Recipe> recipesUsingThis = recipeRepository.getRecipesBySubRecipesContainingAndDeactivated(recipeToDelete, false);

        if (!recipesUsingThis.isEmpty()) {
            List<String> recipeNames = recipesUsingThis.stream().map(Recipe::getName).toList();
            throw new InvalidOperationException("Recipe is used in Recipes as sub-recipe: " + String.join(", ", recipeNames)
                    + ". Remove recipe from all recipes to be able to delete it!");
        }

        recipeToDelete.setDeactivated(true);
        return recipeRepository.save(recipeToDelete);
    }

    @Override
    public List<Recipe> getAllRecipes(String sortBy, String sortDirection) {
        List<Recipe> recipes = recipeRepository.getRecipesByDeactivated(false, SortingUtils.getSort(Recipe.class, sortBy, sortDirection));
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes in the database yet!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getAllRecipesPageable(int page, int pageSize, String sortBy, String sortDirection) {
        Pageable pageable = SortingUtils.getFirstEntry(Recipe.class);
        List<Recipe> recipes = recipeRepository.getRecipesByDeactivated(false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes in the database!");
        }

        pageable = SortingUtils.getPagination(Command.class, sortBy, sortDirection, page, pageSize);
        recipes = recipeRepository.getRecipesByDeactivated(false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any recipes on page " + page + "!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getFullRecipes(String deviceType, String sortBy, String sortDirection) {
        return getFullOrSubRecipes(deviceType, false, sortBy, sortDirection);
    }

    @Override
    public List<Recipe> getSubRecipes(String deviceType, String sortBy, String sortDirection) {
        return getFullOrSubRecipes(deviceType, true, sortBy, sortDirection);
    }

    private List<Recipe> getFullOrSubRecipes(String deviceType, boolean isSubRecipe, String sortBy, String sortDirection) {
        DeviceTypeEnum deviceTypeEnum = DeviceTypeUtils.getDeviceTypeEnum(deviceType);
        List<Recipe> recipes = recipeRepository.getRecipesBySubRecipeAndDeviceTypeAndDeactivated(isSubRecipe, deviceTypeEnum, false, SortingUtils.getSort(Recipe.class, sortBy, sortDirection));
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
        Pageable pageable = SortingUtils.getFirstEntry(Recipe.class);
        String recipeType = isSubRecipe ? "sub-recipes" : "recipes";
        List<Recipe> recipes = recipeRepository.getRecipesBySubRecipeAndDeviceTypeAndDeactivated(isSubRecipe, deviceTypeEnum, false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any " + recipeType + " in the database of type of device: " + deviceType + "!");
        }

        pageable = SortingUtils.getPagination(Recipe.class, sortBy, sortDirection, page, pageSize);
        recipes = recipeRepository.getRecipesBySubRecipeAndDeviceTypeAndDeactivated(isSubRecipe, deviceTypeEnum, false, pageable);
        if (recipes.isEmpty()) {
            throw new NotFoundCustomException("There are not any " + recipeType + " of type of device: " + deviceType + " on page " + page + "!");
        }
        return recipes;
    }

    @Override
    public List<Recipe> getRecipesContainingCommand(Command command) {
        return recipeRepository.getRecipesByCommandsContainingAndDeactivated(command, false);
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
        if (command.getDeviceType() != recipe.getDeviceType()) {
            throw new InvalidOperationException("Types of devices of the command and recipe do not match!"
            + " Recipes device type: " + recipe.getDeviceType()
            + ", Commands device type: " + command.getDeviceType());
        }
        if (command.getName() == null || command.getName().isEmpty()) {
            throw new InvalidOperationException("Command does not have any name set!");
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
        Command commandDb = commandService.getCommandById(commandId);

        if (recipe.getCommands() == null || recipe.getCommands().isEmpty()) {
            throw new NotFoundCustomException("Recipe does not contain any commands!");
        }
        if (index < 0 || index > recipe.getCommands().size() - 1) {
            throw new NotFoundCustomException("Index of command is out of bounds! There are " + recipe.getCommands().size() + " commands in this recipe! Provided index is " + index);
        }

        if (recipe.getCommands().get(index).getId().equals(commandDb.getId())) {
            recipe.getCommands().remove(index);
            return recipeRepository.save(recipe);
        }

        List<String> commandIndexes = IntStream.range(0, recipe.getCommands().size())
                .filter(i -> recipe.getCommands().get(i).getId().equals(commandId))
                .mapToObj(String::valueOf)
                .toList();
        if (commandIndexes.isEmpty()) {
            throw new NotFoundCustomException("Provided recipe does not contain any command with ID '" + commandId + "'!");
        }
        throw new NotFoundCustomException("Command not found on index: " + index + "!"
                + " Commands with this ID can be found on indexes: " + String.join(", ", commandIndexes));
    }

    private boolean recipeWithNameExists(String name) {
        return recipeRepository.getRecipeByNameAndDeactivated(name, false).isPresent();
    }
}
