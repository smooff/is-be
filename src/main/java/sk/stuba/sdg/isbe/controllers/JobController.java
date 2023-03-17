package sk.stuba.sdg.isbe.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.JobService;

@RestController
@RequestMapping("api/jobs/job")
public class JobController {

    @Autowired
    JobService jobService;

    @PostMapping("runFromRecipe/{recipe}")
    public void createJobFromRecipe(@PathVariable Recipe recipe) {
        jobService.runJobFromRecipe(recipe);
    }
}
