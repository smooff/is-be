package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.services.JobService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/job")
public class JobController {

    @Autowired
    private JobService jobService;

    @Operation(summary = "Run a job created from a recipe with repetitions")
    @PostMapping("runFromRecipe/{recipeId}/{repetitions}")
    public Job createJobFromRecipe(@PathVariable String recipeId, @PathVariable int repetitions) {
        return jobService.runJobFromRecipe(recipeId, repetitions);
    }

    @Operation(summary = "Run a job")
    @PostMapping("runJob/{job}/{repetitions}")
    public Job runJob(@PathVariable Job job, @PathVariable int repetitions) {
        return jobService.runJob(job, repetitions);
    }

    @Operation(summary = "Get job by its ID")
    @GetMapping("getJobById/{jobId}")
    public Job getJob(@PathVariable String jobId) {
        return jobService.getJob(jobId);
    }

    @PutMapping("skipCycle/{jobId}")
    public void skipCycle(@PathVariable String jobId) {
        jobService.skipCycle(jobId);
    }

    @PutMapping("skipStep/{jobId}")
    public void skipStep(@PathVariable String jobId) {
        jobService.skipStep(jobId);
    }

    @PutMapping("pauseJob/{jobId}")
    public void pauseJob(@PathVariable String jobId) {
        jobService.pauseJob(jobId);
    }

    @PutMapping("cancelJob/{jobId}")
    public void cancelJob(@PathVariable String jobId) {
        jobService.cancelJob(jobId);
    }

    @PutMapping("continueJob/{jobId}")
    public void continueJob(@PathVariable String jobId) {
        jobService.continueJob(jobId);
    }

    @GetMapping("getFinishedJobs")
    public List<Job> getFinishedJobs() {
        return jobService.getFinishedJobs();
    }

    @GetMapping("getRunningJobs")
    public List<Job> getRunningJobs() {
        return jobService.getRunningJobs();
    }

    @GetMapping("getFinishedJobsByStatus")
    public List<Job> getFinishedJobsByStatus() {
        return jobService.getFinishedJobsByStatus();
    }

    @GetMapping("getRunningJobsByStatus")
    public List<Job> getRunningJobsByStatus() {
        return jobService.getRunningJobsByStatus();
    }

    @GetMapping("getJobByStatus/{status}")
    public List<Job> getJobsByStatus(@PathVariable String status) {
        return jobService.getJobsByStatus(status);
    }
}
