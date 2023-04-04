package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.handlers.exceptions.InvalidEntityException;
import sk.stuba.sdg.isbe.handlers.exceptions.NotFoundCustomException;
import sk.stuba.sdg.isbe.repositories.DeviceRepository;
import sk.stuba.sdg.isbe.services.DeviceService;

import java.util.Optional;

@Service
public class DeviceServiceImpl implements DeviceService {

    private static final String EMPTY_STRING = "";

    @Autowired
    private DeviceRepository deviceRepository;

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

        deviceRepository.save(device);
        return device;
    }

    @Override
    public Device getDevice(String deviceId){
        Optional<Device> optionalDevice = deviceRepository.findById(deviceId);
        if (optionalDevice.isEmpty()) {
            throw new NotFoundCustomException("Recipe with ID: '" + deviceId + "' was not found!");
        }
        return optionalDevice.get();
    }

    @Override
    public ResponseEntity<Device> deleteDevice(String deviceId) {
        Device deviceToDelete = getDevice(deviceId);
        deviceToDelete.setDeactivated(true);
        deviceRepository.save(deviceToDelete);
        return ResponseEntity.ok(deviceToDelete);
    }
}
