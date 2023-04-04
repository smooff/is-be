package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.DeviceService;

@RestController
@RequestMapping("api/jobs/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Operation(summary = "Add new device into the system")
    @PostMapping("create/{device}")
    public Device createDevice(@PathVariable Device device) {
        return deviceService.createDevice(device);
    }

    @Operation(summary = "Delete device by uid")
    @DeleteMapping("delete/{deviceId}")
    public ResponseEntity<Device> deleteDevice(@PathVariable String deviceId) {
        return deviceService.deleteDevice(deviceId);
    }
}
