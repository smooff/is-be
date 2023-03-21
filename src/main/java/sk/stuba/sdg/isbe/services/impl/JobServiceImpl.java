package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidJobException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidRecipeException;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.services.JobService;

import java.util.ArrayList;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Override
    public void runJobFromRecipe(Recipe recipe, int repetitions) {
        if (recipe.isSubRecipe()) {
            throw new InvalidRecipeException("Recipe is only a sub-recipe, can't create a job from it!");
        }

        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        Job job = new Job();
        job.setName("Job: " + recipe.getName());
        job.setCommands(recipe.getCommands());
        job.setStatus(jobStatus);
        job.setNoOfCmds(recipe.getCommands().size());
        job.setNoOfReps(repetitions);

        runJob(job);
    }

    @Override
    public void runJob(Job job) {
        if (!isJobValid(job)) {
            throw new InvalidJobException("Job's body is invalid! Please fill all mandatory fields!");
        }
        jobRepository.save(job);
    }

    @Override
    public List<Job> getFinishedJobs() {
        return getJobByStatus(JobStatusEnum.JOB_DONE);
    }

    @Override
    public List<Job> getRunningJobs() {
        return getJobByStatus(JobStatusEnum.JOB_PROCESSING);
    }

    private List<Job> getJobByStatus(JobStatusEnum status) {
        List<Job> allJobs = jobRepository.findAll();
        List<Job> jobsByStatus = new ArrayList<>();

        for (Job job : allJobs) {
            if (job.getStatus().getRetCode() == status) {
                jobsByStatus.add(job);
            }
        }
        return jobsByStatus;
    }

    private boolean isJobValid(Job job) {
        return job.getName() != null &&
                job.getCommands() != null &&
                !job.getCommands().isEmpty() &&
                job.getNoOfReps() != null;
    }
}
