package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);

    Recipe getRecipeByName(String name);

    List<Recipe> getRecipesByIsSubRecipeAndTypeOfDevice(Boolean isSubRecipe, String typeOfDevice);
}
