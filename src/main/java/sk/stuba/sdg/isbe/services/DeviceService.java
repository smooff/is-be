package sk.stuba.sdg.isbe.services;

import org.springframework.http.ResponseEntity;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;

import java.util.List;

public interface DeviceService {
    Device createDevice(Device device);

    Device initializeDevice(String macAddress);

    Long initExpireTime(String deviceId);

    List<Device> getDevices();

    Device getDeviceById(String deviceId);

    ResponseEntity<Device> deleteDevice(String deviceId);

    ResponseEntity<Job> addJobToDevice(String deviceId, String jodId);

    Device addDataPointTagToDevice(String deviceId, String dataPointTagId);

    List<Job> getAllDeviceJobs(String deviceId);

    List<Job> getPendingDeviceJobs(String deviceId);

    String getDeviceStatus(String deviceId);
}
