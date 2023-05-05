package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Device;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    Device findDeviceByMac(String macAddress);

    Optional<Device> getDeviceByUidAndDeactivated(String deviceId, boolean deactivated);

    List<Device> getDevicesByDeactivated(boolean deactivated);
}
