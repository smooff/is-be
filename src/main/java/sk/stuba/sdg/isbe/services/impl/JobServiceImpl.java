package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidJobException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidRecipeException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
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

    @Override
    public void runJobFromRecipe(String recipeId, int repetitions) {
        Recipe recipe = recipeService.getRecipe(recipeId);
        if (recipe.isSubRecipe()) {
            throw new InvalidRecipeException("Recipe is only a sub-recipe, can't create a job from it!");
        }

        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        Job job = new Job();
        job.setName("Job: " + recipe.getName());
        job.setCommands(recipe.getCommands());
        job.setStatus(jobStatus);
        job.setNoOfCmds(recipe.getCommands().size());
        job.setNoOfReps(repetitions);

        runJob(job);
    }

    @Override
    public void runJob(Job job) {
        if (!job.isValid()) {
            throw new InvalidJobException("Job's body is invalid! Please fill all mandatory fields!");
        }
        job.setCreatedAt(Instant.now().toEpochMilli());
        jobRepository.save(job);
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
        return setJobStatus(job, JobStatusEnum.JOB_CANCELED);
    }

    @Override
    public ResponseEntity<Job> pauseJob(String jobId) {
        Job job = getJob(jobId);
        return setJobStatus(job, JobStatusEnum.JOB_PAUSED);
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

    private ResponseEntity<Job> setJobStatus(Job job, JobStatusEnum jobStatus) {
        if (jobStatus == null) {
            throw new InvalidJobException("Job does not have any status set, therefore it can't be changed!");
        }
        job.getStatus().setCode(jobStatus);
        jobRepository.save(job);

        return ResponseEntity.ok(job);
    }

    private Job getJob(String jobId) {
        Optional<Job> optionalJob = jobRepository.findById(jobId);
        if (optionalJob.isEmpty()) {
            throw new NotFoundCustomException("Job with ID: " + jobId + " was not found!");
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
