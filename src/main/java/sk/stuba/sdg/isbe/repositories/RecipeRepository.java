package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> getRecipesByTypeOfDeviceAndDeactivated(DeviceTypeEnum typeOfDevice, boolean deactivated);

    List<Recipe> getRecipesByTypeOfDeviceAndDeactivated(DeviceTypeEnum typeOfDevice, boolean deactivated, Pageable pageable);

    List<Recipe> getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(boolean isSubRecipe, DeviceTypeEnum typeOfDevice, boolean deactivated);

    List<Recipe> getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(boolean isSubRecipe, DeviceTypeEnum typeOfDevice, boolean deactivated, Pageable pageable);

    Optional<Recipe> getRecipeByNameAndDeactivated(String name, boolean deactivated);

    Optional<Recipe> getRecipeByIdAndDeactivated(String recipeId, boolean deactivated);

    List<Recipe> getRecipesByCommandsContaining(Command command);

    List<Recipe> getRecipesBySubRecipesContaining(Recipe recipe);
}
