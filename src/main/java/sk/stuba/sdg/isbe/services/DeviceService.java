package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.JobStatus;

import java.util.List;

public interface DeviceService {
    Device createDevice(Device device);

    List<Device> getDevices();

    Device getDeviceById(String deviceId);

    ResponseEntity<Device> deleteDevice(String deviceId);

    ResponseEntity<Job> addJobToDevice(String deviceId, String jodId);
}
