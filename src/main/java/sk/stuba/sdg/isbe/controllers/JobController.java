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

    @Operation(summary = "Run a job created from a recipe with n repetitions")
    @PostMapping("runFromRecipe/{recipeId}/{deviceId}/{repetitions}")
    public Job createJobFromRecipe(@PathVariable String recipeId,@PathVariable String deviceId, @PathVariable int repetitions) {
        return jobService.runJobFromRecipe(recipeId, deviceId, repetitions);
    }

    @Operation(summary = "Run a job on given device with n repetitions")
    @PostMapping("runJob/{job}/{deviceId}/{repetitions}")
    public Job runJob(@PathVariable Job job, @PathVariable String deviceId, @PathVariable int repetitions) {
        return jobService.runJob(job, deviceId, repetitions);
    }

    @Operation(summary = "Get job by its ID")
    @GetMapping("getJobById/{jobId}")
    public Job getJob(@PathVariable String jobId) {
        return jobService.getJob(jobId);
    }

    @Operation(summary = "Skip cycle in a job")
    @PutMapping("skipCycle/{jobId}")
    public void skipCycle(@PathVariable String jobId) {
        jobService.skipCycle(jobId);
    }

    @Operation(summary = "Skip step in a job")
    @PutMapping("skipStep/{jobId}")
    public void skipStep(@PathVariable String jobId) {
        jobService.skipStep(jobId);
    }

    @Operation(summary = "Pause a job")
    @PutMapping("pauseJob/{jobId}")
    public void pauseJob(@PathVariable String jobId) {
        jobService.pauseJob(jobId);
    }

    @Operation(summary = "Cancel a job")
    @PutMapping("cancelJob/{jobId}")
    public void cancelJob(@PathVariable String jobId) {
        jobService.cancelJob(jobId);
    }

    @Operation(summary = "Continue job when its paused")
    @PutMapping("continueJob/{jobId}")
    public void continueJob(@PathVariable String jobId) {
        jobService.continueJob(jobId);
    }

    @Operation(summary = "Get all finished jobs")
    @GetMapping("getFinishedJobs")
    public List<Job> getFinishedJobs() {
        return jobService.getFinishedJobs();
    }

    @Operation(summary = "Get all running jobs")
    @GetMapping("getRunningJobs")
    public List<Job> getRunningJobs() {
        return jobService.getRunningJobs();
    }

    @Operation(summary = "Get finished jobs by a given status")
    @GetMapping("getFinishedJobsByStatus")
    public List<Job> getFinishedJobsByStatus() {
        return jobService.getFinishedJobsByStatus();
    }

    @Operation(summary = "Get running jobs by a given status")
    @GetMapping("getRunningJobsByStatus")
    public List<Job> getRunningJobsByStatus() {
        return jobService.getRunningJobsByStatus();
    }

    @Operation(summary = "Get all jobs by a given status")
    @GetMapping("getJobByStatus/{status}")
    public List<Job> getJobsByStatus(@PathVariable String status) {
        return jobService.getAllJobsByStatus(status);
    }
}
