package smarthomesim.demo.device;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import smarthomesim.demo.fan.Fan;
import smarthomesim.demo.light.Light;
import smarthomesim.demo.thermostat.Thermostat;
import smarthomesim.demo.audit.AuditEntry;
import smarthomesim.demo.audit.AuditLog;
import smarthomesim.demo.doorlock.DoorLock;

@RestController
@RequestMapping("/api/devices")

public class DeviceController {

  private final Map<String, Device> devices;
  private final AuditLog auditLog;

    // constructor: Spring will inject the device hub
  public DeviceController(Map<String, Device> devices, AuditLog auditLog) {
    this.devices = devices;
    this.auditLog = auditLog;
  }

  // GET list of all devices with their info
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

  // GET history for a device
  @GetMapping("/{id}/history")
  public List<AuditEntry> getHistory(@PathVariable String id) {
    return auditLog.historyFor(id);
  }
}