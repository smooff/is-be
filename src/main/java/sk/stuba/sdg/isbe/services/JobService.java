package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

public interface JobService {

    void runJobFromRecipe(String recipeId, int repetitions);

    void runJob(Job job);

    ResponseEntity<Job> skipCycle(String jobId);

    ResponseEntity<Job> skipStep(String jobId);

    ResponseEntity<Job> cancelJob(String jobId);

    ResponseEntity<Job> pauseJob(String jobId);

    List<Job> getFinishedJobsByStatus();

    List<Job> getRunningJobsByStatus();

    List<Job> getFinishedJobs();

    List<Job> getRunningJobs();
}
