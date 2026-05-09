package smarthomesim.demo.device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import smarthomesim.demo.audit.AuditEntry;
import smarthomesim.demo.audit.AuditLog;
import smarthomesim.demo.doorlock.DoorLock;
import smarthomesim.demo.events.DeviceRegisteredEvent;
import smarthomesim.demo.events.DeviceRemovedEvent;
import smarthomesim.demo.factory.DeviceFactory;
import smarthomesim.demo.fan.Fan;
import smarthomesim.demo.light.Light;
import smarthomesim.demo.thermostat.Thermostat;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

  // Maps a device type (as sent by the client) to the id-prefix convention; used in seed data
  private static final Map<String, String> ID_PREFIXES = Map.of(
      "Light", "light",
      "Fan", "fan",
      "Thermostat", "thermo",
      "DoorLock", "lock"
  );

  private final Map<String, Device> devices;
  private final DeviceFactory factory;
  private final AuditLog auditLog;
  private final ApplicationEventPublisher publisher;

  public DeviceController(Map<String, Device> devices, DeviceFactory factory, AuditLog auditLog, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.factory = factory;
    this.auditLog = auditLog;
    this.publisher = publisher;
  }

  @GetMapping
  public List<Map<String, Object>> listDevices() {
    List<Map<String, Object>> result = new ArrayList<>();
    for (Device d : devices.values()) {
      Map<String, Object> info = new HashMap<>();
      info.put("id", d.getId());
      info.put("name", d.getName());
      info.put("location", d.getLocation());
      info.put("type", d.getType());

      if (d instanceof Light l) {
        info.put("state", l.getLightState());
        info.put("brightness", l.getBrightness());
        info.put("color", l.getColorHex());
      } else if (d instanceof Fan f) {
        info.put("state", f.getFanState());
        info.put("speed", f.getSpeed());
      } else if (d instanceof Thermostat t) {
        info.put("state", t.getThermostatState());
        info.put("mode", t.getMode());
        info.put("multiplier", t.getMultiplier());
        info.put("desiredTemperature", t.getDesiredTemperature());
        info.put("ambientTemperature", t.getAmbientTemperature());
      } else if (d instanceof DoorLock dl) {
        info.put("state", dl.getDoorLockState());
      }
      result.add(info);
    }
    return result;
  }

  @GetMapping("/{id}/history")
  public List<AuditEntry> getHistory(@PathVariable String id) {
    return auditLog.historyFor(id);
  }

  @PostMapping
  public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
    String type = body.get("type");
    String name = body.get("name");
    String location = body.get("location");
    if (type == null || name == null || location == null) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          "type, name, and location are required");
    }

    String id = nextId(type);
    Device device;
    try {
      device = factory.create(type, id, name, location);
    } catch (IllegalArgumentException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
    }
    devices.put(id, device);
    publisher.publishEvent(new DeviceRegisteredEvent(id, type, name, location));

    Map<String, Object> response = new HashMap<>();
    response.put("id", id);
    response.put("type", type);
    response.put("name", name);
    response.put("location", location);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> remove(@PathVariable String id) {
    Device removed = devices.remove(id);
    if (removed == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "device not found: " + id);
    }
    publisher.publishEvent(new DeviceRemovedEvent(id));
    return ResponseEntity.noContent().build();
  }

  private String nextId(String type) {
    String prefix = ID_PREFIXES.getOrDefault(type, type.toLowerCase());
    int max = devices.keySet().stream()
        .filter(id -> id.startsWith(prefix + "-"))
        .map(id -> id.substring(prefix.length() + 1))
        .filter(s -> s.matches("\\d+"))
        .mapToInt(Integer::parseInt)
        .max()
        .orElse(0);
    return prefix + "-" + (max + 1);
  }
}