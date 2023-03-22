package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    //get RUNNING JOBS
    List<Job> getJobsByStartedAtIsNotAndFinishedAtIs(Long startedAt, Long finishedAt);
    //get FINISHED JOBS
    List<Job> getJobsByFinishedAtIsNot(Long finishedAt);
}
