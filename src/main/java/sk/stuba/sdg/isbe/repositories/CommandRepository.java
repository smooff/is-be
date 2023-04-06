package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.enums.DeviceTypeEnum;
import sk.stuba.sdg.isbe.domain.model.Command;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandRepository extends MongoRepository<Command, String> {
    Optional<Command> getCommandByNameAndDeactivated(String name, boolean deactivated);

    Optional<Command> getCommandByIdAndDeactivated(String id, boolean deactivated);

    List<Command> getCommandsByTypeOfDeviceAndDeactivated(DeviceTypeEnum deviceType, boolean deactivated);
}
