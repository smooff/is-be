package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.enums.JobStatusEnum;
import sk.stuba.sdg.isbe.domain.model.DataPointTag;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidOperationException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.services.DataPointTagService;
import sk.stuba.sdg.isbe.services.DeviceService;
import sk.stuba.sdg.isbe.services.JobService;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String EMPTY_STRING = "";

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
        if (device.getInitExpireTime() < Instant.now().toEpochMilli() || device.getInitExpireTime() == -1) {
            throw new InvalidEntityException("Device initial time is out of time!");
        }

        device.setInitExpireTime((long) -1);
        deviceRepository.save(device);
        
        return device;
    }

    @Override
    public Long initExpireTime(String deviceId) {
        Device device = deviceRepository.findDeviceByUid(deviceId);
        if (device == null) {
            throw new EntityExistsException("Device with id: '" + deviceId + "' was not found!");
        }

        Long time = Instant.now().plus(Duration.ofMinutes(1)).toEpochMilli();
        device.setInitExpireTime(time);

        deviceRepository.save(device);
        return time;
    }

    @Override
    public List<Device> getDevices() {

        return deviceRepository.findAll();
    }

    @Override
    public Device getDeviceById(String deviceId){
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
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
        Job job = jobService.getJob(jobId);

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

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        Device device = optionalDevice.get();

        if (device.getJobs().isEmpty()){
            throw new EntityExistsException("No jobs for device already exists!");
        }
        
        return  device.getJobs();
    }

    @Override
    public List<Job> getPendingDeviceJobs(String deviceId) {
        List<Job> jobs = getAllDeviceJobs(deviceId);
        return jobService.getJobsByStatus(jobs, JobStatusEnum.JOB_PENDING);
    }
}
