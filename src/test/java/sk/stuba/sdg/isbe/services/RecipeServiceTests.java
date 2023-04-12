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

        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
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
        active.setTypeOfDevice(DeviceTypeEnum.ESP32);
        active.setSubRecipe(false);
        Recipe deactivated = new Recipe();
        deactivated.setName("inactiveRecipe " + Instant.now().toEpochMilli());
        deactivated.setDeactivated(true);
        deactivated.setTypeOfDevice(DeviceTypeEnum.ESP32);
        deactivated.setSubRecipe(false);
        recipeService.createRecipe(active);
        recipeService.createRecipe(deactivated);

        List<Recipe> recipeList = recipeService.getFullRecipes("ESP32");
        recipeList.forEach(r -> {
            assertFalse(r.isDeactivated());
            assertFalse(r.isSubRecipe());
        });

        recipeList = recipeService.getSubRecipes("ESP32");
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
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);
        recipeService.deleteRecipe(recipe.getId());
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.getRecipeById(recipe.getId());
        });
        String expected = "Recipe with ID: '" + recipe.getId() + "' was not found!";
        assertEquals(expected, exception.getMessage());
        recipeRepository.delete(recipe);
    }

    @Test
    void testAddCommandToRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);

        Command command = new Command();
        command.setName("command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
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
        Recipe recipeFromDb = recipeService.getRecipeById(recipe.getId());
        assertNotNull(recipeFromDb.getCommands());
        assertFalse(recipeFromDb.getCommands().isEmpty());

        recipeRepository.delete(recipe);
        commandRepository.delete(command);
    }

    @Test
    void testUpdateRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        Recipe recipe2 = new Recipe();
        recipe2.setName("recipe2 " + Instant.now().toEpochMilli());
        recipe2.setSubRecipe(false);
        recipe2.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        Recipe updateRecipe = new Recipe();
        updateRecipe.setName(recipe2.getName());

        String expected = "Recipe with name: '" + updateRecipe.getName() + "' already exists!";
        Exception exception = assertThrows(EntityExistsException.class, () -> {
            recipeService.updateRecipe(recipe.getId(), updateRecipe);
        });
        assertEquals(expected, exception.getMessage());

        updateRecipe.setName("updatedRecipe " + Instant.now().toEpochMilli());
        updateRecipe.setSubRecipes(List.of(recipe2));
        updateRecipe.setSubRecipe(true);
        updateRecipe.setTypeOfDevice(DeviceTypeEnum.SDG_CUBE);
        recipeService.updateRecipe(recipe.getId(), updateRecipe);
        Recipe recipeDb = recipeService.getRecipeById(recipe.getId());
        assertNotNull(recipeDb);
        assertEquals(recipeDb.getName(), updateRecipe.getName());
        assertEquals(recipeDb.isSubRecipe(), updateRecipe.isSubRecipe());

        recipeRepository.delete(recipeDb);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testAddSubRecipeToRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        Recipe recipe2 = new Recipe();
        recipe2.setName("recipe2 " + Instant.now().toEpochMilli());
        recipe2.setSubRecipe(false);
        recipe2.setTypeOfDevice(DeviceTypeEnum.SDG_CUBE);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        String expected = "Device types of the recipes do not match!"
                + " Recipe's device type: " + recipe.getTypeOfDevice()
                + " Sub-recipe's device type: " + recipe2.getTypeOfDevice();
        Exception exception = assertThrows(InvalidEntityException.class, () -> {
            recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        });
        assertEquals(expected, exception.getMessage());
        recipe2.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeRepository.save(recipe2);

        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        Recipe recipeDb = recipeService.getRecipeById(recipe.getId());
        assertEquals(1, recipeDb.getSubRecipes().size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void testRemoveSubRecipeFromRecipe() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        Recipe recipe2 = new Recipe();
        recipe2.setName("recipe2 " + Instant.now().toEpochMilli());
        recipe2.setSubRecipe(false);
        recipe2.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);
        recipeService.createRecipe(recipe2);

        String expected = "ID of sub-recipe must not be null!";
        Exception exception = assertThrows(NullPointerException.class, () -> {
            recipeService.removeSubRecipeFromRecipe(recipe.getId(), null, 0);
        });
        assertEquals(expected, exception.getMessage());

        expected = "Provided recipe does not contain any sub-recipes!";
        exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0);
        });
        assertEquals(expected, exception.getMessage());

        Recipe subRecipe = new Recipe();
        subRecipe.setName("asd" + Instant.now().toEpochMilli());
        subRecipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        subRecipe.setSubRecipe(true);
        recipeService.createRecipe(subRecipe);
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());

        expected = "Provided recipe does not contain any sub-recipe with ID '" + recipe2.getId() + "'!";
        exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0);
        });
        assertEquals(expected, exception.getMessage());

        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        recipeService.addSubRecipeToRecipe(recipe.getId(), recipe2.getId());
        expected = "Sub-recipe not found on index: " + 0 + "! Sub-recipes with this ID can be found on indexes: " + String.join(", ", String.join(", ", List.of("1","2")) + ".");
        exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 0);
        });
        assertEquals(expected, exception.getMessage());

        recipeService.removeSubRecipeFromRecipe(recipe.getId(), recipe2.getId(), 1);
        Recipe recipeDb = recipeService.getRecipeById(recipe.getId());
        assertEquals(2, recipeDb.getSubRecipes().size());

        recipeRepository.delete(recipe);
        recipeRepository.delete(recipe2);
    }

    @Test
    void removeCommandFromRecipeTest() {
        Recipe recipe = new Recipe();
        recipe.setName("recipe " + Instant.now().toEpochMilli());
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);

        Command command = new Command();
        command.setName("command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        command.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command);

        Command command1 = new Command();
        command1.setName("command" + Instant.now().toEpochMilli());
        command1.setParams(List.of(1,2,3));
        command1.setTypeOfDevice(DeviceTypeEnum.ESP32);
        commandService.createCommand(command1);

        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        recipeService.addCommandToRecipe(recipe.getId(), command.getId());
        recipeService.addCommandToRecipe(recipe.getId(), command1.getId());

        String expected = "Command not found on index: 2! Commands with this ID can be found on indexes: 0, 1";
        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.removeCommandFromRecipe(recipe.getId(), command.getId(), 2);
        });
        assertEquals(expected, exception.getMessage());

        expected = "Provided recipe does not contain any command with ID 'fakeId'!";
        exception = assertThrows(NotFoundCustomException.class, () -> {
            recipeService.removeCommandFromRecipe(recipe.getId(), "fakeId", 2);
        });
        assertEquals(expected, exception.getMessage());

        recipeRepository.delete(recipe);
        commandRepository.delete(command);
        commandRepository.delete(command1);
    }
}
