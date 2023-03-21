package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.JobService;

import java.util.List;

@RestController
@RequestMapping("api/jobs/job")
public class JobController {

    @Autowired
    JobService jobService;

    @PostMapping("runFromRecipe/{recipe}/{repetitions}")
    public void createJobFromRecipe(@PathVariable Recipe recipe, @PathVariable int repetitions) {
        jobService.runJobFromRecipe(recipe, repetitions);
    }

    @PostMapping("runJob/{job}")
    public void runJob(@PathVariable Job job) {
        jobService.runJob(job);
    }

    @GetMapping("getFinishedJobs")
    public List<Job> getFinishedJobs() {
        return jobService.getFinishedJobs();
    }

    @GetMapping("getRunningJobs")
    public List<Job> getRunningJobs() {
        return jobService.getRunningJobs();
    }
}
