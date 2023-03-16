package sk.stuba.sdg.isbe.services.repository.job;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.entities.job.Job;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
}
