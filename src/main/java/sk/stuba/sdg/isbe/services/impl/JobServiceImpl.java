package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.repositories.JobStatusRepository;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;
import sk.stuba.sdg.isbe.services.JobStatusService;
import sk.stuba.sdg.isbe.services.RecipeService;
import sk.stuba.sdg.isbe.utilities.JobStatusUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private JobStatusService jobStatusService;

    @Autowired
    private JobStatusRepository jobStatusRepository;

    @Override
    public Job runJobFromRecipe(String recipeId, String deviceId, int repetitions) {
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
        return runJob(job, deviceId, repetitions);
    }

    @Override
    public Job runJob(Job job, String deviceId, int repetitions) {
        if (repetitions < 0) {
            throw new IllegalArgumentException("Repetitions must be equal to or greater than 0!");
        }

        job.setNoOfReps(repetitions);
        if (!job.isValid()) {
            throw new InvalidEntityException("Job's body is invalid! Please fill all mandatory fields!");
        }

        // try if jobs stack is full
        if (deviceService.getDeviceById(deviceId).getJobs().size() >= 10 ) {
            throw new InvalidEntityException("Job can't be add! Full device jobs stack.");
        }

        // create jobStatus for this new created job
        JobStatus jobStatus = new JobStatus();
        jobStatus.setRetCode(JobStatusEnum.JOB_PENDING);
        jobStatus.setCode(JobStatusEnum.JOB_FREE);
        job.setStatus(jobStatus);
        jobStatusService.createJobStatus(jobStatus);

        job.setCreatedAt(Instant.now().toEpochMilli());
        jobRepository.save(job);

        // add job as running on device by device uid
        deviceService.addJobToDevice(deviceId, job.getUid());
        return job;
    }

    private void addCommandsFromRecipes(Job job, Recipe recipe) {
        job.setCommands(new ArrayList<>());
        addCommandsRecursively(job, recipe);

        if (job.getCommands().isEmpty()) {
            throw new InvalidEntityException("The recipe and its sub-recipes do not contain any commands!");
        }
        job.setNoOfCmds(job.getCommands().size());
    }

    private void addCommandsRecursively(Job job, Recipe recipe) {
        if (recipe.getCommands() != null && !recipe.getCommands().isEmpty()) {
            job.getCommands().addAll(recipe.getCommands());
        }
        if (recipe.getSubRecipes() == null) {
            return;
        }

        for (Recipe subRecipe : recipe.getSubRecipes()) {
            addCommandsRecursively(job, subRecipe);
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
        jobStatusRepository.save(job.getStatus());

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
        jobStatusRepository.save(job.getStatus());

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
        return getJobsByStatus("JOB_DONE");
    }

    @Override
    public List<Job> getRunningJobsByStatus() {
        return getJobsByStatus("JOB_PROCESSING");
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

    @Override
    public List<Job> getJobsByStatus(String status) {
        JobStatusEnum jobStatus = JobStatusUtils.getJobStatusEnum(status);
        List<Job> allJobs = jobRepository.findAll();
        return allJobs.stream().filter(job -> job.getStatus().getRetCode() == jobStatus).collect(Collectors.toList());
    }
}
