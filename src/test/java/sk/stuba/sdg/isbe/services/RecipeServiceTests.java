package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("!deployment")
public class RecipeServiceTests {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private CommandRepository commandRepository;

    private static final String NONE = "NONE";

    @Test
    void testCreateRecipeInvalidEntity() {
        Recipe recipe = new Recipe();
        Exception exception = assertThrows(InvalidEntityException.class, () -> recipeService.createRecipe(recipe));
        assertEquals("Recipe has no name set!", exception.getMessage());

        recipe.setName("recipe" + Instant.now().toEpochMilli());
        exception = assertThrows(InvalidEntityException.class, () -> recipeService.createRecipe(recipe));
        assertEquals("Type of device for recipe is missing!", exception.getMessage());

        recipe.setDeviceType(DeviceTypeEnum.ESP32);
        exception = assertThrows(InvalidEntityException.class, () -> recipeService.createRecipe(recipe));
        assertEquals("Recipe's sub-recipe status not defined!", exception.getMessage());

        recipe.setSubRecipe(false);
        recipeService.createRecipe(recipe);
        exception = assertThrows(InvalidEntityException.class, () -> recipeService.createRecipe(recipe));
        assertEquals("Recipe with name: '" + recipe.getName() + "' already exists!", exception.getMessage());

        recipeRepository.delete(recipe);
    }

    @Test
    void testOnlyActiveRecipesReturned() {
        Recipe active = new Recipe("activeRecipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        active.setDeactivated(false);

        Recipe deactivated = new Recipe("inactiveRecipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        deactivated.setDeactivated(true);
        recipeService.createRecipe(active);
        recipeService.createRecipe(deactivated);

        List<Recipe> recipeList = recipeService.getFullRecipes(DeviceTypeEnum.ESP32.name(), NONE, NONE);
        recipeList.forEach(r -> {
            assertFalse(r.isDeactivated());
            assertFalse(r.isSubRecipe());
        });

        recipeList = recipeService.getSubRecipes(DeviceTypeEnum.ESP32.name(), NONE, NONE);
        recipeList.forEach(r -> {
            assertFalse(r.isDeactivated());
            assertTrue(r.isSubRecipe());
        });

        recipeRepository.delete(active);
        recipeRepository.delete(deactivated);
    }

    @Test
    void testGetRecipesByDeviceType() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);
        Recipe recipe2 = new Recipe("recipe2 " + Instant.now().toEpochMilli(), DeviceTypeEnum.SDG_CUBE, false);
        recipeService.createRecipe(recipe2);

        List<Recipe> recipes = recipeService.getRecipesByDeviceType(DeviceTypeEnum.ESP32.name(), NONE, NONE);
        assertTrue(recipes.stream().allMatch(r -> r.getDeviceType() == DeviceTypeEnum.ESP32));

        recipes = recipeService.getRecipesByDeviceType(DeviceTypeEnum.SDG_CUBE.name(), NONE, NONE);
        assertTrue(recipes.stream().allMatch(r -> r.getDeviceType() == DeviceTypeEnum.SDG_CUBE));

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testGetRecipesByDeviceTypePageable() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);
        Recipe recipe2 = new Recipe("recipe2 " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe2);

        List<Recipe> recipes = recipeService.getRecipesByDeviceTypePageable(DeviceTypeEnum.ESP32.name(), 1, 2, "", NONE);
        assertEquals(2, recipes.size());

        recipes = recipeService.getRecipesByDeviceTypePageable(DeviceTypeEnum.ESP32.name(), 1, 1, "", NONE);
        assertEquals(1, recipes.size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testDeleteRecipe() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);
        recipeService.deleteRecipe(recipe.getId());
        Exception exception = assertThrows(NotFoundCustomException.class, () -> recipeService.getRecipeById(recipe.getId()));
        assertEquals("Recipe with ID: '" + recipe.getId() + "' was not found!", exception.getMessage());
        recipeRepository.delete(recipe);
    }

    @Test
    void testAddCommandToRecipe() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);

        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);
        commandService.deleteCommand(command.getId());

        Exception exception = assertThrows(NotFoundCustomException.class, () -> recipeService.addCommandToRecipe(recipe.getId(), command.getId()));
        assertEquals("Command with ID: '" + command.getId() + "' was not found!", exception.getMessage());

        commandService.createCommand(command);

        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        Recipe recipeFromDb = recipeService.getRecipeById(recipe.getId());
        assertNotNull(recipeFromDb.getCommands());
        assertFalse(recipeFromDb.getCommands().isEmpty());

        recipeRepository.delete(recipe);
        commandRepository.delete(command);
    }

    @Test
    void testUpdateRecipe() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        Recipe recipe2 = new Recipe("recipe2 " + Instant.now().toEpochMilli(), DeviceTypeEnum.SDG_CUBE, false);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.SDG_CUBE);
        commandService.createCommand(command);

        String recipeId = recipe.getId();
        Exception exception = assertThrows(EntityExistsException.class, () -> recipeService.updateRecipe(recipeId, new Recipe(recipe2.getName(), DeviceTypeEnum.ESP32, false)));
        assertEquals("Recipe with name: '" + recipe2.getName() + "' already exists!", exception.getMessage());

        Recipe updateRecipe = new Recipe("updatedRecipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.SDG_CUBE, true);
        updateRecipe.setCommands(List.of(command));
        updateRecipe.setSubRecipes(List.of(recipe2));
        recipeService.updateRecipe(recipe.getId(), updateRecipe);
        recipe = recipeService.getRecipeById(recipe.getId());
        assertNotNull(recipe);
        assertEquals(recipe.getName(), updateRecipe.getName());
        assertEquals(recipe.isSubRecipe(), updateRecipe.isSubRecipe());
        assertEquals(recipe.getDeviceType(), updateRecipe.getDeviceType());
        assertEquals(1, recipe.getCommands().size());
        assertEquals(1, recipe.getSubRecipes().size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testAddSubRecipeToRecipe() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        Recipe recipe2 = new Recipe("recipe2 " + Instant.now().toEpochMilli(), DeviceTypeEnum.SDG_CUBE, false);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        String expected = "Device types of the recipes do not match!"
                + " Recipe's device type: " + recipe.getDeviceType()
                + ", Sub-recipe's device type: " + recipe2.getDeviceType();
        Exception exception = assertThrows(InvalidEntityException.class, () -> recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId()));
        assertEquals(expected, exception.getMessage());

        recipe2.setDeviceType(DeviceTypeEnum.ESP32);
        recipeService.updateRecipe(recipe2.getId(), recipe2);

        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        Recipe recipeDb = recipeService.getRecipeById(recipe.getId());
        assertEquals(1, recipeDb.getSubRecipes().size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testRemoveSubRecipeFromRecipe() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        Recipe recipe2 = new Recipe("recipe2 " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        Exception exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeSubRecipeFromRecipe(recipe.getId(), null, 0));
        assertEquals("Recipe with ID: 'null' was not found!", exception.getMessage());

        exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0));
        assertEquals("Provided recipe does not contain any sub-recipes!", exception.getMessage());

        Recipe subRecipe = new Recipe("asd" + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, true);
        recipeService.createRecipe(subRecipe);
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());

        exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0));
        assertEquals("Provided recipe does not contain any sub-recipe with ID '" + recipe2.getId() + "'!", exception.getMessage());

        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0));
        assertEquals("Sub-recipe not found on index: " + 0 + "! Sub-recipes with this ID can be found on indexes: " + String.join(", ", String.join(", ", List.of("1","2")) + "."),
                      exception.getMessage());

        recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 1);
        Recipe recipeDb = recipeService.getRecipeById(recipe.getId());
        assertEquals(2, recipeDb.getSubRecipes().size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void removeCommandFromRecipeTest() {
        Recipe recipe = new Recipe("recipe " + Instant.now().toEpochMilli(), DeviceTypeEnum.ESP32, false);
        recipeService.createRecipe(recipe);

        Command command = new Command("command" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        Command command1 = new Command("command1" + Instant.now().toEpochMilli(), List.of(1,2,3), DeviceTypeEnum.ESP32);
        commandService.createCommand(command1);

        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        recipeService.addCommandToRecipe(recipe.getId(), command1.getId());

        Exception exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeCommandFromRecipe(recipe.getId(), command.getId(), 2));
        assertEquals("Command not found on index: 2! Commands with this ID can be found on indexes: 0, 1", exception.getMessage());

        recipeService.removeCommandFromRecipe(recipe.getId(), command1.getId(), 2);

        exception = assertThrows(NotFoundCustomException.class, () -> recipeService.removeCommandFromRecipe(recipe.getId(), command1.getId(), 1));
        assertEquals("Provided recipe does not contain any command with ID '" + command1.getId() + "'!", exception.getMessage());

        recipeRepository.delete(recipe);
        commandRepository.delete(command);
        commandRepository.delete(command1);
    }
}
