package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.services.JobService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/job")
public class JobController {

    @Autowired
    JobService jobService;

    @PostMapping("runFromRecipe/{recipeId}/{repetitions}")
    public void createJobFromRecipe(@PathVariable String recipeId, @PathVariable int repetitions) {
        jobService.runJobFromRecipe(recipeId, repetitions);
    }

    @PostMapping("runJob/{job}")
    public void runJob(@PathVariable Job job) {
        jobService.runJob(job);
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
}
