package smarthomesim.demo.thermostat;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import smarthomesim.demo.audit.AuditLog;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.persistence.DeviceStore;

@RestController
@RequestMapping("/api/thermostats")

public class ThermostatController {

  // fields
  private final Map<String, Device> devices;
  private final DeviceStore store;
  private final AuditLog auditLog;

  // constructor: Spring will inject the device hub
  public ThermostatController(Map<String, Device> devices, DeviceStore store, AuditLog auditLog) {
    this.devices = devices;
    this.store = store;
    this.auditLog = auditLog;
  }

  // GET info about a thermostat
  @GetMapping("/{id}")
  public Map<String, Object> getThermostat(@PathVariable String id) {
    return thermostatInfo((Thermostat) devices.get(id));
  }

  // POST for toggle
  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleThermostat(@PathVariable String id) {
    Thermostat t = (Thermostat) devices.get(id);
    t.togglePower();
    auditLog.record(id, "toggle");
    store.save();
    return thermostatInfo(t);
  }

  // POST for mode
  @PostMapping("/{id}/mode")
  public Map<String, Object> setMode(@PathVariable String id, @RequestBody Map<String, String> body) {
    Thermostat thermostat = (Thermostat) devices.get(id);
    ThermostatMode mode = ThermostatMode.valueOf(body.get("mode"));
    thermostat.setStateMode(mode);
    auditLog.record(id, "setMode" + body.get("mode"));
    store.save();
    return thermostatInfo(thermostat);
  }

  // POST for set temperature
  @PostMapping("/{id}/temperature")
  public Map<String, Object> setTemperature(@PathVariable String id, @RequestBody Map<String, Integer> body) {
    Thermostat thermostat = (Thermostat) devices.get(id);
    thermostat.setStateDesiredTemperature(body.get("temperature"));
    auditLog.record(id, "setTemperature" + body.get("temperature"));
    store.save();
    return thermostatInfo(thermostat);
  }
  
  // POST for multiplier
  @PostMapping("/{id}/multiplier")
  public Map<String, Object> setMultiplier(@PathVariable String id, @RequestBody Map<String, String> body) {
    Thermostat thermostat = (Thermostat) devices.get(id);
    Multiplier multiplier = Multiplier.valueOf(body.get("multiplier"));
    thermostat.setMultiplier(multiplier);
    auditLog.record(id, "setMultiplier" + body.get("multiplier"));
    store.save();
    return thermostatInfo(thermostat);
  }

  private Map<String, Object> thermostatInfo(Thermostat t) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", t.getId());
    m.put("name", t.getName());
    m.put("location", t.getLocation());
    m.put("type", t.getType());
    m.put("state", t.getThermostatState());
    m.put("mode", t.getMode());
    m.put("desiredTemperature", t.getDesiredTemperature());
    m.put("ambientTemperature", t.getAmbientTemperature());
    m.put("multiplier", t.getMultiplier());
    return m;
  }
}
