package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RecipeServiceTests {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private CommandRepository commandRepository;

    @Test
    void testCreateRecipeInvalidEntity() {
        Recipe recipe = new Recipe();
        Exception exception = assertThrows(InvalidEntityException.class, () -> {
            recipeService.createRecipe(recipe);
        });
        String expected = "Recipe has no name set!";
        assertEquals(expected, exception.getMessage());

        recipe.setName("recipe" + Instant.now().toEpochMilli());
        exception = assertThrows(InvalidEntityException.class, () -> {
            recipeService.createRecipe(recipe);
        });
        expected = "Type of device for recipe is missing!";
        assertEquals(expected, exception.getMessage());

        recipe.setTypeOfDevice("device");
        exception = assertThrows(InvalidEntityException.class, () -> {
            recipeService.createRecipe(recipe);
        });
        expected = "Recipe's sub-recipe status not defined!";
        assertEquals(expected, exception.getMessage());

        recipe.setSubRecipe(false);
        recipeService.createRecipe(recipe);
        exception = assertThrows(InvalidEntityException.class, () -> {
            recipeService.createRecipe(recipe);
        });
        expected = "Recipe with name: '" + recipe.getName() + "' already exists!";
        assertEquals(expected, exception.getMessage());

        recipeRepository.delete(recipe);
    }

    @Test
    void testOnlyActiveRecipesReturned() {
        Recipe active = new Recipe();
        active.setName("activeRecipe " + Instant.now().toEpochMilli());
        active.setDeactivated(false);
        active.setTypeOfDevice("device");
        active.setSubRecipe(false);
        Recipe deactivated = new Recipe();
        deactivated.setName("inactiveRecipe " + Instant.now().toEpochMilli());
        deactivated.setDeactivated(true);
        deactivated.setTypeOfDevice("device");
        deactivated.setSubRecipe(false);
        recipeService.createRecipe(active);
        recipeService.createRecipe(deactivated);

        List<Recipe> recipeList = recipeService.getFullRecipes("device");
        recipeList.forEach(r -> {
            assertFalse(r.isDeactivated());
            assertFalse(r.isSubRecipe());
        });

        recipeList = recipeService.getSubRecipes("device");
        recipeList.forEach(r -> {
            assertFalse(r.isDeactivated());
            assertTrue(r.isSubRecipe());
        });

        recipeRepository.delete(active);
        recipeRepository.delete(deactivated);
    }

    @Test
    void testDeleteRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice("device");
        recipeService.createRecipe(recipe);
        recipeService.deleteRecipe(recipe.getId());
        Recipe recipeFromDb = recipeService.getRecipe(recipe.getId());
        assertTrue(recipeFromDb.isDeactivated());
        recipeRepository.delete(recipe);
    }

    @Test
    void testAddCommandToRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice("device");
        recipeService.createRecipe(recipe);

        Command command = new Command();
        command.setName("command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        commandService.createCommand(command);
        commandService.deleteCommand(command.getId());

        String expected = "Command with ID: '" + command.getId() + "' was not found!";
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        });
        assertEquals(expected, exception.getMessage());

        command.setDeactivated(false);
        commandRepository.save(command);

        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        Recipe recipeFromDb = recipeService.getRecipe(recipe.getId());
        assertNotNull(recipeFromDb.getCommands());
        assertFalse(recipeFromDb.getCommands().isEmpty());

        recipeRepository.delete(recipe);
        commandRepository.delete(command);
    }
}
