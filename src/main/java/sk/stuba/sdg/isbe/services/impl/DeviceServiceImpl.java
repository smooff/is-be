package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.handlers.exceptions.*;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.services.DataPointTagService;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String EMPTY_STRING = "";
    private static final String NONE = "NONE";

    @Value("${sdg.http.auth-token-header-name}")
    private String apiKey;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobService jobService;

    @Autowired
    private DataPointTagService dataPointTagService;

    @Override
    public Device createDevice(Device device) {
        if (device.getName() == null || device.getName().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Device has no name set!");
        }
        if (device.getMac() == null || device.getMac().equals(EMPTY_STRING)) {
            throw new InvalidEntityException("Device has no mac address set!");
        }
        if (device.getType() == null) {
            throw new InvalidEntityException("Device has no type set!");
        }

        device.setInitExpireTime((long) -1);
        device.setAddTime(Instant.now().toEpochMilli());
        deviceRepository.save(device);

        return device;
    }

    @Override
    public Device initializeDevice(String macAddress) {
        if (macAddress == null || macAddress.isEmpty()) {
            throw new InvalidOperationException("Mac address is not set!");
        }

        Device device = deviceRepository.findDeviceByMac(macAddress);
        if (device == null) {
            throw new EntityExistsException("Device with MAC: '" + macAddress + "' was not found!");
        }
        if (device.getInitExpireTime() == -1) {
            throw new InvalidEntityException("Device is initialize!");
        }
        if (device.getInitExpireTime() < Instant.now().toEpochMilli()) {
            throw new InvalidEntityException("Device initial time is out of!");
        }

        device.setInitExpireTime((long) -1);
        deviceRepository.save(device);

        device.setDataPointTags(null);
        device.setInitApiKey(apiKey);

        return device;
    }

    @Override
    public Long initExpireTime(String deviceId) {
        Device device = getDeviceById(deviceId);
        Long time = Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli();
        device.setInitExpireTime(time);

        deviceRepository.save(device);
        return time;
    }

    @Override
    public List<Device> getDevices() {
        return deviceRepository.getDevicesByDeactivated(false);
    }

    @Override
    public Device getDeviceById(String deviceId){
        Optional<Device> optionalDevice = deviceRepository.getDeviceByUidAndDeactivated(deviceId, false);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        return optionalDevice.get();
    }

    @Override
    public ResponseEntity<Device> deleteDevice(String deviceId) {
        Device deviceToDelete = getDeviceById(deviceId);
        deviceToDelete.setDeactivated(true);
        deviceRepository.save(deviceToDelete);
        return ResponseEntity.ok(deviceToDelete);
    }

    @Override
    public ResponseEntity<Job> addJobToDevice(String deviceId, String jobId){
        Job job = jobService.getJobById(jobId);

        Device device = getDeviceById(deviceId);
        device.getJobs().add(job);

        deviceRepository.save(device);
        return ResponseEntity.ok(job);
    }

    @Override
    public Device addDataPointTagToDevice(String deviceId, String dataPointTagId) {
        DataPointTag dataPointTag = dataPointTagService.getDataPointTagById(dataPointTagId);
        
        Device device = getDeviceById(deviceId);
        device.getDataPointTags().add(dataPointTag);

        return deviceRepository.save(device);
    }

    @Override
    public List<Job> getAllDeviceJobs(String deviceId){
        Device device = getDeviceById(deviceId);

        if (device.getJobs().isEmpty()){
            throw new EntityExistsException("No jobs for device already exists!");
        }
        
        return device.getJobs();
    }

    @Override
    public List<Job> getPendingDeviceJobs(String deviceId) {
        return jobService.getAllJobsByStatus(deviceId, JobStatusEnum.JOB_PENDING.name(), NONE, NONE);
    }

    @Override
    public String getDeviceStatus(String deviceId) {
        Device device = getDeviceById(deviceId);
        List<Job> runningJobs = jobService.getAllJobsByStatus(deviceId, JobStatusEnum.JOB_PROCESSING.name(), NONE, NONE);
        LocalDateTime lastUpdated = runningJobs.get(0).getStatus().getLastUpdated();

        if (LocalDateTime.now().minusSeconds(device.getResponseTime()).isAfter(lastUpdated)) {
            throw new DeviceErrorException("Device job last updated at: " + lastUpdated.toString().replace("T", " - ")
                    + ". Device may be disconnected!");
        }
        return lastUpdated.toString().replace("T", " - ");
    }
}
