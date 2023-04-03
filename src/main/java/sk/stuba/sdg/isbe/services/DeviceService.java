package sk.stuba.sdg.isbe.services;

import sk.stuba.sdg.isbe.domain.model.Device;

public interface DeviceService {
    Device createDevice(Device device);

    Device getDevice(String deviceId);
}
