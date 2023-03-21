package sk.stuba.sdg.isbe.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import sk.stuba.sdg.isbe.controllers.JobController;
import sk.stuba.sdg.isbe.controllers.RecipeController;

@Configuration
public class DataInitConfiguration {

    @Autowired
    private RecipeController recipeController;

    @Autowired
    private JobController jobController;

    @Bean
    void test(){
        //Job done = new Job();
        //done.setName("doneJob");
        //JobStatus jobStatus = new JobStatus();
        //jobStatus.setRetCode(JobStatusEnum.JOB_DONE);
        //done.setStatus(jobStatus);
        //jobController.runJob(done);
    }
}
