package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Command;

import java.util.Optional;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
    Command getCommandByNameAndDeactivated(String name, boolean deactivated);

    Optional<Command> getCommandByIdAndDeactivated(String id, boolean deactivated);
}
