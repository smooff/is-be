package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

public interface JobService {

    Job runJobFromRecipe(String recipeId, String deviceId, int repetitions);

    Job runJob(Job job, String deviceId, int repetitions);

    Job getJob(String jobId);

    ResponseEntity<Job> skipCycle(String jobId);

    ResponseEntity<Job> skipStep(String jobId);

    ResponseEntity<Job> cancelJob(String jobId);

    ResponseEntity<Job> pauseJob(String jobId);

    ResponseEntity<Job> continueJob(String jobId);

    List<Job> getFinishedJobsByStatus();

    List<Job> getRunningJobsByStatus();

    List<Job> getFinishedJobs();

    List<Job> getRunningJobs();

    List<Job> getJobsByStatus(String status);
}
