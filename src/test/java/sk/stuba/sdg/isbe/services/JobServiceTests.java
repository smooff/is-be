package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.repositories.CommandRepository;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.repositories.RecipeRepository;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JobServiceTests {

    @Autowired
    private JobService jobService;

    @Autowired
    protected JobRepository jobRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CommandService commandService;

    @Autowired
    private CommandRepository commandRepository;

    @Test
    void testRunJobFromRecipe() {
        Recipe recipe = new Recipe();
        recipe.setSubRecipe(true);
        recipe.setName("Recipe" + Instant.now().toEpochMilli());
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), -1);
        });
        String expected = "Repetitions must be equal to or greater than 0!";
        assertEquals(expected, exception.getMessage());

        exception = assertThrows(InvalidEntityException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), 0);
        });
        expected = "Recipe is only a sub-recipe, can't create a job from it!";
        assertEquals(expected, exception.getMessage());

        recipe.setSubRecipe(false);
        recipeService.updateRecipe(recipe.getId(), recipe);
        exception = assertThrows(InvalidEntityException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), 1);
        });
        expected = "The recipe and its sub-recipes do not contain any commands!";
        assertEquals(expected, exception.getMessage());

        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        commandService.createCommand(command);

        recipe.setCommandIds(List.of(command.getId()));
        recipeService.updateRecipe(recipe.getId(), recipe);

        Recipe subRecipe = new Recipe();
        subRecipe.setSubRecipe(true);
        subRecipe.setName("SubRecipe" + Instant.now().toEpochMilli());
        subRecipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        subRecipe.setCommandIds(List.of(command.getId(), command.getId()));
        recipeService.createRecipe(subRecipe);
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());

        Recipe subSubRecipe = new Recipe();
        subSubRecipe.setSubRecipe(false);
        subSubRecipe.setName("SubSubRecipe" + Instant.now().toEpochMilli());
        subSubRecipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        subSubRecipe.setCommandIds(List.of(command.getId()));
        recipeService.createRecipe(subSubRecipe);

        recipeService.addSubRecipeToRecipe(subRecipe.getId(), subSubRecipe.getId());

        Job job = jobService.runJobFromRecipe(recipe.getId(), 1);
        assertEquals(7, job.getCommands().size());

        commandRepository.delete(command);
        recipeRepository.delete(recipe);
        recipeRepository.delete(subRecipe);
        recipeRepository.delete(subSubRecipe);
        jobRepository.delete(job);
    }

    @Test
    void testSkipCycle() {
        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));

        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);
        jobStatus.setCurrentCycle(1);

        Job job = new Job();
        job.setName("Job" + Instant.now().toEpochMilli());
        job.setCommands(List.of(command));
        job.setNoOfReps(1);
        job.setStatus(jobStatus);

        jobService.runJob(job, 0);
        Job jobDb = jobService.skipCycle(job.getUid()).getBody();
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentCycle());
    }

    @Test
    void testSkipStep() {
        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));

        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);
        jobStatus.setCurrentStep(1);

        Job job = new Job();
        job.setName("Job" + Instant.now().toEpochMilli());
        job.setCommands(List.of(command));
        job.setNoOfReps(1);
        job.setStatus(jobStatus);

        jobService.runJob(job, 0);
        Job jobDb = jobService.skipStep(job.getUid()).getBody();
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentStep());
    }
}
