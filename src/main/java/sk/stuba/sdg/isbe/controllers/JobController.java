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
    public Job createJobFromRecipe(@PathVariable String recipeId, @PathVariable String deviceId, @PathVariable int repetitions) {
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

    @Operation(summary = "Get all jobs on a device")
    @GetMapping("getAllJobsOnDevice/{deviceId}")
    public List<Job> getAllJobsOnDevice(@PathVariable String deviceId) {
        return jobService.getAllJobsOnDevice(deviceId);
    }

    @Operation(summary = "Get all jobs on a device with pagination and sorting")
    @GetMapping("getAllJobsOnDevicePageable/{deviceId}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Job> getAllJobsOnDevicePageable(@PathVariable String deviceId, @PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return jobService.getAllJobsOnDevicePageable(deviceId, page, pageSize, sortBy, sortDirection);
    }

    @Operation(summary = "Get finished jobs on a device")
    @GetMapping("getFinishedJobs/{deviceId}")
    public List<Job> getFinishedJobsByStatus(@PathVariable String deviceId) {
        return jobService.getFinishedJobsByStatus(deviceId);
    }

    @Operation(summary = "Get running jobs on a device")
    @GetMapping("getRunningJobs/{deviceId}")
    public List<Job> getRunningJobsByStatus(@PathVariable String deviceId) {
        return jobService.getRunningJobsByStatus(deviceId);
    }

    @Operation(summary = "Get all jobs by a given status")
    @GetMapping("getJobsByStatus/{deviceId}/{status}")
    public List<Job> getJobsByStatus(@PathVariable String deviceId, @PathVariable String status) {
        return jobService.getAllJobsByStatus(deviceId, status);
    }

    @Operation(summary = "Get all jobs by a given status with pagination and sorting")
    @GetMapping("getAllJobsByStatusPageable/{deviceId}/{status}/{page}/{pageSize}/{sortBy}/{sortDirection}")
    public List<Job> getAllJobsByStatusPageable(@PathVariable String deviceId, @PathVariable String status, @PathVariable int page, @PathVariable int pageSize, @PathVariable String sortBy, @PathVariable String sortDirection) {
        return jobService.getAllJobsByStatusPageable(deviceId, status, page, pageSize, sortBy, sortDirection);
    }
}
