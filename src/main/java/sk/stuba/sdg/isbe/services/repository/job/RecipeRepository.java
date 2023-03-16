package sk.stuba.sdg.isbe.services.repository.job;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.entities.job.Recipe;

import java.util.List;

@Repository
public interface RecipeRepository extends MongoRepository<Recipe, String> {

    List<Recipe> getRecipesByTypeOfDevice(String typeOfDevice);
}
