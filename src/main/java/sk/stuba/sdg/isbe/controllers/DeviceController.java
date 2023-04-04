package sk.stuba.sdg.isbe.controllers;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sk.stuba.sdg.isbe.domain.model.Device;
import sk.stuba.sdg.isbe.domain.model.Job;
import sk.stuba.sdg.isbe.domain.model.Recipe;
import sk.stuba.sdg.isbe.services.DeviceService;

import java.util.List;

@RestController
@RequestMapping("api/device")
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @GetMapping
    public List<Device> getDevices() {
        return this.deviceService.getDevices();
    }

    @Operation(summary = "Add new device into the system")
    @PostMapping("/create")
    public Device createDevice(@Valid @RequestBody Device device) {
        return this.deviceService.createDevice(device);
    }

    @Operation(summary = "Delete device by uid")
    @DeleteMapping("delete/{deviceId}")
    public ResponseEntity<Device> deleteDevice(@PathVariable String deviceId) {
        return this.deviceService.deleteDevice(deviceId);
    }

    @PostMapping(value = "/getDeviceById/{deviceId}")
    public Device getDeviceById(@PathVariable String deviceId) {
        return this.deviceService.getDeviceById(deviceId);
    }

    @PutMapping("addJobToDevice/{deviceId}/{jobId}")
    public ResponseEntity<Job> addJobToDevice(@PathVariable String deviceId, @PathVariable String jobId) {
        return this.deviceService.addJobToDevice(deviceId, jobId);
    }
}
