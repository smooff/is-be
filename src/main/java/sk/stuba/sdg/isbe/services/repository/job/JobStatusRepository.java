package sk.stuba.sdg.isbe.services.repository.job;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.entities.job.JobStatus;

@Repository
public interface JobStatusRepository extends MongoRepository<JobStatus, String> {
}
