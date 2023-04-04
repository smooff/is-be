package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Device;

public interface DeviceService {
    Device createDevice(Device device);

    Device getDevice(String deviceId);

    ResponseEntity<Device> deleteDevice(String deviceId);
}
