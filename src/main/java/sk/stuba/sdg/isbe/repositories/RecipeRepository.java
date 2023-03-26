package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> getRecipesByTypeOfDeviceAndDeactivated(DeviceTypeEnum typeOfDevice, boolean deactivated);

    Recipe getRecipeByNameAndDeactivated(String name, boolean deactivated);

    List<Recipe> getRecipesByIsSubRecipeAndTypeOfDeviceAndDeactivated(Boolean isSubRecipe, DeviceTypeEnum typeOfDevice, boolean deactivated);

    List<Recipe> getRecipesByCommandsContaining(Command command);
}
