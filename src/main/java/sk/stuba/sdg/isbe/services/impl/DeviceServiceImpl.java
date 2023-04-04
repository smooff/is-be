package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.handlers.exceptions.EntityExistsException;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.repositories.JobRepository;
import sk.stuba.sdg.isbe.services.DeviceService;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private JobRepository jobRepository;

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

        device.setUid(UUID.randomUUID().toString());
        device.setAddAt(Instant.now().toEpochMilli());
        deviceRepository.save(device);

        return device;
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
        Job job = jobRepository.getJobByUid(jobId);
        if (job.getUid().isEmpty()) {
            throw new NotFoundCustomException("Job with ID: '" + jobId + "' was not found! Can't be add.");
        }

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        Device device = optionalDevice.get();
        device.getJobs().add(job);

        deviceRepository.save(device);
        return ResponseEntity.ok(job);
    }

    @Override
    public ResponseEntity<List<Job>> getAllDeviceJobs(String deviceId){

        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Device with ID: '" + deviceId + "' was not found!");
        }
        Device device = optionalDevice.get();

        if (device.getJobs().isEmpty()){
            throw new EntityExistsException("No jobs for device already exists!");
        }
        
        return  ResponseEntity.ok(device.getJobs());
    }
}
