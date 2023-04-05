package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

public interface JobService {

    Job runJobFromRecipe(String recipeId, String deviceId, int repetitions);

    Job runJob(Job job, String deviceId, int repetitions);

    Job getJob(String jobId);

    Job skipCycle(String jobId);

    Job skipStep(String jobId);

    Job cancelJob(String jobId);

    Job pauseJob(String jobId);

    Job continueJob(String jobId);

    List<Job> getFinishedJobsByStatus();

    List<Job> getRunningJobsByStatus();

    List<Job> getFinishedJobs();

    List<Job> getRunningJobs();

    List<Job> getJobsByStatus(String status);
}
