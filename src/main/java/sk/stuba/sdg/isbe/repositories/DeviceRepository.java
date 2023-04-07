package sk.stuba.sdg.isbe.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import sk.stuba.sdg.isbe.domain.model.Device;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {
    Device findDeviceByMac(String macAddress);

    Device findDeviceByUid(String deviceId);
}
