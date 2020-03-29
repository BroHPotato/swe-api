package com.redroundrobin.thirema.apirest.controller;

import com.redroundrobin.thirema.apirest.models.postgres.Device;
import com.redroundrobin.thirema.apirest.models.postgres.Sensor;
import com.redroundrobin.thirema.apirest.models.postgres.User;
import com.redroundrobin.thirema.apirest.service.postgres.DeviceService;
import com.redroundrobin.thirema.apirest.service.postgres.SensorService;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = {"/devices"})
public class DeviceController extends CoreController {

  private DeviceService deviceService;

  private SensorService sensorService;

  @Autowired
  public DeviceController(DeviceService deviceService, SensorService sensorService) {
    this.deviceService = deviceService;
    this.sensorService = sensorService;
  }

  // Get all devices optionally filtered by entityId
  @GetMapping(value = {""})
  public ResponseEntity<List<Device>> getDevices(
      @RequestHeader("Authorization") String authorization,
      @RequestParam(value = "entity", required = false) Integer entityId) {
    User user = this.getUserFromAuthorization(authorization);
    if (user.getType() == User.Role.ADMIN) {
      if (entityId != null) {
        return ResponseEntity.ok(deviceService.findAllByEntityId(entityId));
      } else {
        return ResponseEntity.ok(deviceService.findAll());
      }
    } else {
      if (entityId == null || user.getEntity().getId() == entityId) {
        return ResponseEntity.ok(deviceService.findAllByEntityId(user.getEntity().getId()));
      } else {
        return ResponseEntity.ok(Collections.emptyList());
      }
    }
  }

  // Get device by deviceId
  @GetMapping(value = {"/{deviceId:.+}"})
  public ResponseEntity<Device> getDevice(@RequestHeader("Authorization") String authorization,
                          @PathVariable("deviceId") int deviceId) {
    User user = this.getUserFromAuthorization(authorization);
    if (user.getType() == User.Role.ADMIN) {
      return ResponseEntity.ok(deviceService.findById(deviceId));
    } else {
      return ResponseEntity.ok(
          deviceService.findByIdAndEntityId(deviceId, user.getEntity().getId()));
    }
  }

  // Get all sensors by deviceId
  @GetMapping(value = {"/{deviceId:.+}/sensors"})
  public ResponseEntity<List<Sensor>> getSensorsByDevice(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("deviceId") int deviceId) {
    User user = this.getUserFromAuthorization(authorization);
    if (user.getType() == User.Role.ADMIN) {
      return ResponseEntity.ok(sensorService.findAllByDeviceId(deviceId));
    } else {
      return ResponseEntity.ok(
          sensorService.findAllByDeviceIdAndEntityId(deviceId, user.getEntity().getId()));
    }
  }

  // Get sensor by deviceId and realSensorId
  @GetMapping(value = {"/{deviceId:.+}/sensor/{realSensorId:.+}"})
  public ResponseEntity<Sensor> getSensorByDevice(
      @RequestHeader("Authorization") String authorization,
      @PathVariable("deviceId") int deviceId,
      @PathVariable("realSensorId") int realSensorId) {
    User user = this.getUserFromAuthorization(authorization);
    if (user.getType() == User.Role.ADMIN) {
      return ResponseEntity.ok(sensorService.findByDeviceIdAndRealSensorId(deviceId, realSensorId));
    } else {
      return ResponseEntity.ok(sensorService.findByDeviceIdAndRealSensorIdAndEntityId(deviceId,
          realSensorId, user.getEntity().getId()));
    }
  }
}