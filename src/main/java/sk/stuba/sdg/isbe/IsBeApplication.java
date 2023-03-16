package sk.stuba.sdg.isbe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import sk.stuba.sdg.isbe.entities.job.Command;
import sk.stuba.sdg.isbe.entities.job.Recipe;
import sk.stuba.sdg.isbe.services.service.job.RecipeService;

import java.util.List;

@SpringBootApplication
public class IsBeApplication {

	@Autowired
	private RecipeService recipeService;

	public static void main(String[] args) {
		SpringApplication.run(IsBeApplication.class, args);
	}

	@Bean
	CommandLineRunner runner() {
		return args -> {
			Recipe recipe = new Recipe();
			recipe.setName("OtherRecipe");
			recipe.setSubRecipe(false);
			recipe.setTypeOfDevice("ALL");

			Command command = new Command();
			command.setName("command");
			command.setParams(List.of(1,2,3));
			recipe.setCommands(List.of(command));
			recipeService.createRecipe(recipe);
			//recipeService.runJobFromRecipe(recipe);
			System.out.println();
		};
	}

}
