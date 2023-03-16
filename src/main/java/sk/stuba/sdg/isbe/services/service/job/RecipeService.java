package sk.stuba.sdg.isbe.services.service.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.entities.job.Job;
import sk.stuba.sdg.isbe.entities.job.JobStatus;
import sk.stuba.sdg.isbe.entities.job.Recipe;
import sk.stuba.sdg.isbe.entities.job.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.services.repository.job.JobRepository;
import sk.stuba.sdg.isbe.services.repository.job.RecipeRepository;

import java.util.List;

@Service
public class RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private JobRepository jobRepository;

    public void createRecipe(Recipe recipe) {
        recipeRepository.save(recipe);
    }

    public List<Recipe> getRecipeByTypeOfDevice(String typeOfDevice) {
        return recipeRepository.getRecipeByTypeOfDevice(typeOfDevice);
    }

    public void runJobFromRecipe(Recipe recipe) {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        Job job = new Job();
        job.setName("Job from " + recipe.getName());
        job.setCommands(recipe.getCommands());
        job.setStatus(jobStatus);
        job.setNoOfCmds(1);
        job.setNoOfReps(1);

        jobRepository.save(job);
    }
}
