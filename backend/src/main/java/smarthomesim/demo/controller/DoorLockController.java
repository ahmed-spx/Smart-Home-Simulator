package smarthomesim.demo.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;

import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.doorlock.DoorLock;
import smarthomesim.demo.domain.events.DeviceStateChangedEvent;
import smarthomesim.demo.domain.exception.DeviceNotFoundException;

@RestController
@RequestMapping("/api/doorlocks")
public class DoorLockController {

  private final Map<String, Device> devices;
  private final ApplicationEventPublisher publisher;

  public DoorLockController(Map<String, Device> devices, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.publisher = publisher;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getDoorLock(@PathVariable String id) {
    return doorLockInfo(requireLock(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleDoorLock(@PathVariable String id) {
    DoorLock doorLock = requireLock(id);
    doorLock.toggleLock();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggleLock"));
    return doorLockInfo(doorLock);
  }

  private DoorLock requireLock(String id) {
    Device d = devices.get(id);
    if (!(d instanceof DoorLock dl)) throw new DeviceNotFoundException(id);
    return dl;
  }

  private Map<String, Object> doorLockInfo(DoorLock dl) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", dl.getId());
    m.put("name", dl.getName());
    m.put("location", dl.getLocation());
    m.put("type", dl.getType());
    m.put("state", dl.getDoorLockState());
    return m;
  }
}