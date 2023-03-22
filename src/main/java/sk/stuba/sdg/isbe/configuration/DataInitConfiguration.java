package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.controllers.RecipeController;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;
import sk.stuba.sdg.isbe.repositories.JobRepository;

import java.time.Instant;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private JobController jobController;

    @Autowired
    private JobRepository jobRepository;

    @Bean
    void test(){
        jobController.cancelJob("641b4cdf7e8de258cdf6169b");
        Job done = new Job();
        JobStatus jobStatus = new JobStatus();
        jobStatus.setCurrentCycle(1);
        jobStatus.setCode(JobStatusEnum.JOB_PROCESSING);
        done.setName("newestJob");
        done.setStartedAt(Instant.now().toEpochMilli());
        done.setCreatedAt(Instant.now().toEpochMilli());
        done.setStatus(jobStatus);
        jobRepository.save(done);
        jobController.skipCycle(done.getUid());
        //done.setName("doneJob");
        //JobStatus jobStatus = new JobStatus();
        //jobStatus.setRetCode(JobStatusEnum.JOB_DONE);
        //done.setStatus(jobStatus);
        //jobController.runJob(done);
    }
}
