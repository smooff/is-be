package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Recipe;

import java.util.List;

public interface JobService {

    void runJobFromRecipe(Recipe recipe, int repetitions);

    void runJob(Job job);

    List<Job> getFinishedJobs();

    List<Job> getRunningJobs();
}
