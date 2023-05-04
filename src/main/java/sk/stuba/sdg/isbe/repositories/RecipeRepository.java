package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> getRecipesByDeactivated(boolean deactivated, Sort sort);

    List<Recipe> getRecipesByDeactivated(boolean deactivated, Pageable pageable);

    List<Recipe> getRecipesByDeviceTypeAndDeactivated(DeviceTypeEnum deviceType, boolean deactivated, Sort sort);

    List<Recipe> getRecipesByDeviceTypeAndDeactivated(DeviceTypeEnum deviceType, boolean deactivated, Pageable pageable);

    List<Recipe> getRecipesBySubRecipeAndDeviceTypeAndDeactivated(boolean isSubRecipe, DeviceTypeEnum deviceType, boolean deactivated, Sort sort);

    List<Recipe> getRecipesBySubRecipeAndDeviceTypeAndDeactivated(boolean isSubRecipe, DeviceTypeEnum deviceType, boolean deactivated, Pageable pageable);

    Optional<Recipe> getRecipeByNameAndDeactivated(String name, boolean deactivated);

    Optional<Recipe> getRecipeByIdAndDeactivated(String recipeId, boolean deactivated);

    List<Recipe> getRecipesByCommandsContainingAndDeactivated(Command command, boolean deactivated);

    List<Recipe> getRecipesBySubRecipesContainingAndDeactivated(Recipe recipe, boolean deactivated);
}
