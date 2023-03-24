package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.CommandController;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.controllers.RecipeController;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.time.Instant;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private JobController jobController;

    @Autowired
    private CommandController commandController;

    @Bean
    void addRecipes() {
        Recipe active = new Recipe();
        active.setName("activeRecipe " + Instant.now().toEpochMilli());
        active.setDeactivated(false);
        active.setTypeOfDevice("device");
        active.setSubRecipe(false);
    }
}
