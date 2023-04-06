package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.*;
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
        if (recipe.isDeactivated()) {
            throw new InvalidEntityException("Recipe is deactivated, can't create a job from it!");
        }
        if (recipe.isSubRecipe()) {
            throw new InvalidEntityException("Recipe is only a sub-recipe, can't create a job from it!");
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

        Device device = deviceService.getDeviceById(deviceId);

        // try if jobs stack is full
        if (device.getJobs().size() >= 10) {
            throw new InvalidEntityException("Job can't be added! Job stack of device is full.");
        }

        // create jobStatus for this new created job
        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        // add dataPoints
        jobStatus.setData(getDataPointsFromDevice(device));
        job.setStatus(jobStatus);
        jobStatusService.createJobStatus(jobStatus);

        job.setCreatedAt(Instant.now().toEpochMilli());
        jobRepository.save(job);

        // add job as running on device by device uid
        deviceService.addJobToDevice(deviceId, job.getUid());
        return job;
    }

    private List<DataPoint> getDataPointsFromDevice(Device device) {
        List<DataPoint> dataPoints = new ArrayList<>();
        for (DataPointTag tag : device.getDataPointTags()) {
            DataPoint dataPoint = new DataPoint();
            dataPoint.setTag(tag.getTag());
            dataPoints.add(dataPoint);
        }
        return dataPoints;
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
    public Job skipCycle(String jobId) {
        Job job = getJob(jobId);
        Integer currentCycle = job.getStatus().getCurrentCycle();
        Integer maxCycles = job.getNoOfReps();

        if (Objects.equals(currentCycle, maxCycles)) {
            throw new InvalidOperationException("Last cycle is under process, can't skip current one!");
        }
        job.getStatus().setCurrentCycle(currentCycle + 1);
        jobStatusRepository.save(job.getStatus());

        return job;
    }

    @Override
    public Job skipStep(String jobId) {
        Job job = getJob(jobId);
        Integer currentStep = job.getStatus().getCurrentStep();
        Integer maxSteps = job.getStatus().getTotalSteps();

        if (Objects.equals(currentStep, maxSteps)) {
            throw new InvalidOperationException("Last step is under process, can't skip current one!");
        }
        job.getStatus().setCurrentStep(currentStep + 1);
        jobStatusRepository.save(job.getStatus());

        return job;
    }

    @Override
    public Job cancelJob(String jobId) {
        Job job = getJob(jobId);
        job.setToCancel(true);
        return jobRepository.save(job);
    }

    @Override
    public Job pauseJob(String jobId) {
        Job job = getJob(jobId);
        job.setPaused(true);
        return jobRepository.save(job);
    }

    @Override
    public Job continueJob(String jobId) {
        Job job = getJob(jobId);
        job.setPaused(false);
        return jobRepository.save(job);
    }

    @Override
    public List<Job> getFinishedJobsByStatus() {
        return getAllJobsByStatus("JOB_DONE");
    }

    @Override
    public List<Job> getRunningJobsByStatus() {
        return getAllJobsByStatus("JOB_PROCESSING");
    }

    @Override
    public List<Job> getFinishedJobs() {
        List<Job> finishedJobs = jobRepository.getJobsByFinishedAtIsNot(null);
        if (finishedJobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any finished jobs in the database!");
        }
        return finishedJobs;
    }

    @Override
    public List<Job> getRunningJobs() {
        List<Job> runningJobs = jobRepository.getJobsByStartedAtIsNotAndFinishedAtIs(null, null);
        if (runningJobs.isEmpty()) {
            throw new NotFoundCustomException("There are not any running jobs in the database!");
        }
        return runningJobs;
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
    public List<Job> getAllJobsByStatus(String status) {
        JobStatusEnum jobStatus = JobStatusUtils.getJobStatusEnum(status);
        List<Job> jobsByStatus = getJobsByStatus(jobRepository.findAll(), jobStatus);
        if (jobsByStatus.isEmpty()) {
            throw new NotFoundCustomException("There are no jobs with status: '" + status + "' in the database!");
        }
        return jobsByStatus;
    }

    public List<Job> getJobsByStatus(List<Job> jobs, JobStatusEnum status) {
        return jobs.stream().filter(job -> job.getStatus().getCode() == status).toList();
    }
}
