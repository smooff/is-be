package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.CommandController;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.controllers.RecipeController;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private JobController jobController;

    @Autowired
    private CommandController commandController;

    @Bean
    void testJobs() {
        //Recipe recipe = new Recipe();
        //recipe.setName("cleaning");
        //recipe.setTypeOfDevice("device1");
        //recipe.setSubRecipe(false);
        ////recipeController.createRecipe(recipe);
        //Recipe subRecipe = new Recipe();
        //subRecipe.setName("subCleaning");
        //subRecipe.setTypeOfDevice("device1");
        //subRecipe.setSubRecipe(true);
        ////recipeController.createRecipe(subRecipe);
        ////recipeController.addSubRecipeToRecipe("641c20667fc75218a2a499a4", "641c209ae60bf01560292576");
        //Recipe changeRecipe = new Recipe();
        //Command command = new Command();
        //command.setName("command");
        //command.setParams(List.of(1,2,3));
        //Command command2 = new Command();
        //command2.setParams(List.of(3,2,1));
        //command2.setName("command2");
        //changeRecipe.setCommands(List.of(command, command2));
        ////recipeController.removeCommandFromRecipe("641c4e8a0f95d0023921adb5", "641c556a1374123f72bc6242");
        ////recipeController.updateRecipe("641c1cd2b17054653f5632f7", changeRecipe);
        ////recipeController.removeSubRecipeFromRecipe("641c20667fc75218a2a499a4", "641c209ae60bf01560292576");
        ////recipeController.updateRecipe("641c1cd2b17054653f5632f7", changeRecipe);
        ////recipeController.updateRecipe("641c20667fc75218a2a499a4", changeRecipe);
        ////jobController.createJobFromRecipe("641c20667fc75218a2a499a4", 0);
        ////recipeController.deleteRecipe("641c209ae60bf01560292576");
        ////recipeController.createRecipe(recipe);
        ////recipeController.addSubRecipeToRecipe("641c2cb44637eb77a7e33f91", "641c209ae60bf01560292576");
        ////recipeController.removeSubRecipeFromRecipe("641c2cb44637eb77a7e33f91", "641c209ae60bf01560292576");
        //System.out.println();
    }
}
