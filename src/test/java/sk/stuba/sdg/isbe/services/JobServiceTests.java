package sk.stuba.sdg.isbe.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.*;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Profile("!deployment")
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

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Test
    void testRunJobFromRecipe() {
        Recipe recipe = new Recipe();
        recipe.setSubRecipe(true);
        recipe.setName("Recipe" + Instant.now().toEpochMilli());
        recipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        recipeService.createRecipe(recipe);

        Device device = new Device();
        device.setName("device1" + Instant.now().toEpochMilli());
        device.setMac("ABCD");
        device.setType(DeviceTypeEnum.ESP32);
        deviceService.createDevice(device);

        Exception exception = assertThrows(InvalidEntityException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), device.getUid(), 0);
        });
        String expected = "Recipe is only a sub-recipe, can't create a job from it!";
        assertEquals(expected, exception.getMessage());

        recipe.setSubRecipe(false);
        recipeService.updateRecipe(recipe.getId(), recipe);
        exception = assertThrows(InvalidEntityException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), device.getUid(), 1);
        });
        expected = "The recipe and its sub-recipes do not contain any commands!";
        assertEquals(expected, exception.getMessage());

        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));
        commandService.createCommand(command);

        recipe.setCommands(List.of(command));
        recipeService.updateRecipe(recipe.getId(), recipe);

        exception = assertThrows(IllegalArgumentException.class, () -> {
            jobService.runJobFromRecipe(recipe.getId(), device.getUid(), -1);
        });
        expected = "Repetitions must be equal to or greater than 0!";
        assertEquals(expected, exception.getMessage());

        Recipe subRecipe = new Recipe();
        subRecipe.setSubRecipe(true);
        subRecipe.setName("SubRecipe" + Instant.now().toEpochMilli());
        subRecipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        subRecipe.setCommands(List.of(command, command));
        recipeService.createRecipe(subRecipe);
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());
        recipeService.addSubRecipeToRecipe(recipe.getId(), subRecipe.getId());

        Recipe subSubRecipe = new Recipe();
        subSubRecipe.setSubRecipe(false);
        subSubRecipe.setName("SubSubRecipe" + Instant.now().toEpochMilli());
        subSubRecipe.setTypeOfDevice(DeviceTypeEnum.ESP32);
        subSubRecipe.setCommands(List.of(command));
        recipeService.createRecipe(subSubRecipe);

        recipeService.addSubRecipeToRecipe(subRecipe.getId(), subSubRecipe.getId());

        Job job = jobService.runJobFromRecipe(recipe.getId(), device.getUid(), 1);
        assertEquals(7, job.getCommands().size());

        commandRepository.delete(command);
        recipeRepository.delete(recipe);
        recipeRepository.delete(subRecipe);
        recipeRepository.delete(subSubRecipe);
        jobRepository.delete(job);
        jobStatusRepository.delete(job.getStatus());
        deviceRepository.delete(device);
    }

    @Test
    void testSkipCycle() {
        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));

        Device device = new Device();
        device.setName("device1" + Instant.now().toEpochMilli());
        device.setMac("ABCD");
        device.setType(DeviceTypeEnum.ESP32);
        deviceService.createDevice(device);

        Job job = new Job();
        job.setName("Job" + Instant.now().toEpochMilli());
        job.setCommands(List.of(command));

        jobService.runJob(job, device.getUid(), 0);
        job.getStatus().setCurrentCycle(1);
        jobStatusRepository.save(job.getStatus());
        Job jobDb = jobService.skipCycle(job.getUid());
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentCycle());

        jobRepository.delete(job);
        jobStatusRepository.delete(job.getStatus());
        deviceRepository.delete(device);
    }

    @Test
    void testSkipStep() {
        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));

        Device device = new Device();
        device.setName("device1" + Instant.now().toEpochMilli());
        device.setMac("ABCD");
        device.setType(DeviceTypeEnum.ESP32);
        deviceService.createDevice(device);

        Job job = new Job();
        job.setName("Job" + Instant.now().toEpochMilli());
        job.setCommands(List.of(command));
        job.setNoOfReps(1);

        jobService.runJob(job, device.getUid(), 0);
        job.getStatus().setTotalSteps(3);
        job.getStatus().setCurrentStep(1);
        jobStatusRepository.save(job.getStatus());
        Job jobDb = jobService.skipStep(job.getUid());
        assertNotNull(jobDb);
        assertEquals(2, jobDb.getStatus().getCurrentStep());

        jobRepository.delete(job);
        jobStatusRepository.delete(job.getStatus());
        deviceRepository.delete(device);
    }

    @Test
    void testGetJobsByStatus() {
        Command command = new Command();
        command.setName("Command" + Instant.now().toEpochMilli());
        command.setParams(List.of(1,2,3));

        Device device = new Device();
        device.setName("device1" + Instant.now().toEpochMilli());
        device.setMac("ABCD");
        device.setType(DeviceTypeEnum.ESP32);
        deviceService.createDevice(device);

        Job job = new Job();
        job.setName("Job" + Instant.now().toEpochMilli());
        job.setCommands(List.of(command));
        job.setNoOfReps(1);

        Job job2 = new Job();
        job2.setName("Job" + Instant.now().toEpochMilli());
        job2.setCommands(List.of(command));
        job2.setNoOfReps(1);

        jobService.runJob(job, device.getUid(), 0);
        jobService.runJob(job2, device.getUid(), 0);

        Exception exception = assertThrows(NotFoundCustomException.class, () -> {
            jobService.getJobsByStatus("WRONG_STATUS");
        });
        String expected = "Job status: '" + "WRONG_STATUS" + "' does not exist!";
        assertEquals(expected, exception.getMessage());

        List<Job> pendingJobs = jobService.getJobsByStatus("JOB_PENDING");
        assertFalse(pendingJobs.isEmpty());

        jobRepository.delete(job);
        jobRepository.delete(job2);
        jobStatusRepository.delete(job.getStatus());
        jobStatusRepository.delete(job2.getStatus());
        deviceRepository.delete(device);
    }
}
