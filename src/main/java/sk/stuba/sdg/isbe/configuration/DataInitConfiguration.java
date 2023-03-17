package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.util.List;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    JobController jobController;
    @Bean
    void initRecipe(){
        Recipe recipe = new Recipe();
        recipe.setName("NewRecipe");
        recipe.setSubRecipe(false);
        recipe.setTypeOfDevice("ALL");

        Command command = new Command();
        command.setName("command");
        command.setParams(List.of(1,2,3));
        recipe.setCommands(List.of(command));
        //recipeService.createRecipe(recipe);
        //recipeService.runJobFromRecipe(recipe);

        jobController.createJobFromRecipe(recipeService.getRecipesByTypeOfDevice("ALL").get(0));
        System.out.println();
    }
}
