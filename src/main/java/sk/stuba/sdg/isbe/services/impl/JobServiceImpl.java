package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.services.CommandService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.RecipeService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private CommandService commandService;

    @Override
    public Job runJobFromRecipe(String recipeId, int repetitions) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        if (recipe.isSubRecipe()) {
            throw new InvalidEntityException("Recipe is only a sub-recipe, can't create a job from it!");
        }
        if (recipe.isDeactivated()) {
            throw new InvalidEntityException("Recipe is deactivated, can't create a job from it!");
        }

        Job job = new Job();
        job.setName("Job: " + recipe.getName());
        addCommandsFromRecipes(job, recipe);

        return runJob(job, repetitions);
    }

    @Override
    public Job runJob(Job job, int repetitions) {
        if (repetitions < 0) {
            throw new IllegalArgumentException("Repetitions must be equal to or greater than 0!");
        }
        if (!job.isValid()) {
            throw new InvalidEntityException("Job's body is invalid! Please fill all mandatory fields!");
        }
        job.setNoOfReps(repetitions);
        job.setCreatedAt(Instant.now().toEpochMilli());
        return jobRepository.save(job);
    }

    private void addCommandsFromRecipes(Job job, Recipe recipe) {
        addCommandsRecursively(job, recipe);

        if (job.getCommands().isEmpty()) {
            throw new InvalidEntityException("The recipe and its sub-recipes do not contain any commands!");
        }
        job.setNoOfCmds(job.getCommands().size());
    }

    private void addCommandsRecursively(Job job, Recipe recipe) {
        if (job.getCommands() == null) {
            job.setCommands(new ArrayList<>());
        }

        if (recipe.getCommandIds() != null) {
            recipe.getCommandIds().forEach(cId -> job.getCommands().add(commandService.getCommandById(cId)));
        }

        if (recipe.getSubRecipeIds() != null) {
            for (String subRecipeId : recipe.getSubRecipeIds()) {
                Recipe subRecipe = recipeService.getRecipe(subRecipeId);
                if (subRecipe.getCommandIds() != null && !subRecipe.getCommandIds().isEmpty()) {
                    addCommandsRecursively(job, subRecipe);
                }
            }
        }
    }

    @Override
    public ResponseEntity<Job> skipCycle(String jobId) {
        Job job = getJob(jobId);
        Integer currentCycle = job.getStatus().getCurrentCycle();
        Integer maxCycles = job.getNoOfReps();

        if (Objects.equals(currentCycle, maxCycles)) {
            throw new InvalidOperationException("Last cycle is under process, can't skip current one!");
        }
        job.getStatus().setCurrentCycle(currentCycle + 1);
        jobRepository.save(job);

        return ResponseEntity.ok(job);
    }

    @Override
    public ResponseEntity<Job> skipStep(String jobId) {
        Job job = getJob(jobId);
        Integer currentStep = job.getStatus().getCurrentStep();
        Integer maxSteps = job.getStatus().getTotalSteps();

        if (Objects.equals(currentStep, maxSteps)) {
            throw new InvalidOperationException("Last step is under process, can't skip current one!");
        }
        job.getStatus().setCurrentStep(currentStep + 1);
        jobRepository.save(job);

        return ResponseEntity.ok(job);
    }

    @Override
    public ResponseEntity<Job> cancelJob(String jobId) {
        Job job = getJob(jobId);
        job.setToCancel(true);
        jobRepository.save(job);
        return ResponseEntity.ok(job);
    }

    @Override
    public ResponseEntity<Job> pauseJob(String jobId) {
        Job job = getJob(jobId);
        job.setPaused(true);
        jobRepository.save(job);
        return ResponseEntity.ok(job);
    }

    @Override
    public ResponseEntity<Job> continueJob(String jobId) {
        Job job = getJob(jobId);
        job.setPaused(false);
        jobRepository.save(job);
        return ResponseEntity.ok(job);
    }

    @Override
    public List<Job> getFinishedJobsByStatus() {
        return getJobByStatus(JobStatusEnum.JOB_DONE);
    }

    @Override
    public List<Job> getRunningJobsByStatus() {
        return getJobByStatus(JobStatusEnum.JOB_PROCESSING);
    }

    @Override
    public List<Job> getFinishedJobs() {
        return jobRepository.getJobsByFinishedAtIsNot(null);
    }

    @Override
    public List<Job> getRunningJobs() {
        return jobRepository.getJobsByStartedAtIsNotAndFinishedAtIs(null, null);
    }

    @Override
    public Job getJob(String jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            throw new NotFoundCustomException("Job with ID: '" + jobId + "' was not found!");
        }
        return optionalJob.get();
    }

    private List<Job> getJobByStatus(JobStatusEnum status) {
        List<Job> allJobs = jobRepository.findAll();
        List<Job> jobsByStatus = new ArrayList<>();

        for (Job job : allJobs) {
            if (job.getStatus().getRetCode() == status) {
                jobsByStatus.add(job);
            }
        }
        return jobsByStatus;
    }
}
