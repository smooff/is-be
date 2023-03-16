package sk.stuba.sdg.isbe.services.repository.job;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.entities.job.Command;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
}
