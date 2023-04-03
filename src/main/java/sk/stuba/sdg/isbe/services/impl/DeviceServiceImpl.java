package sk.stuba.sdg.isbe.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Recipe;
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
}
