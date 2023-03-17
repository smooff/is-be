package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.services.JobService;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    public void runJobFromRecipe(Recipe recipe) {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setCode(JobStatusEnum.JOB_PENDING);

        Job job = new Job();
        job.setName("Job from " + recipe.getName());
        job.setCommands(recipe.getCommands());
        job.setStatus(jobStatus);
        job.setNoOfCmds(1);
        job.setNoOfReps(1);

        jobRepository.save(job);
    }
}
