package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Job;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
}
