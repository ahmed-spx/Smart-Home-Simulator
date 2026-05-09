package smarthomesim.demo.thermostat;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.events.DeviceStateChangedEvent;

@RestController
@RequestMapping("/api/thermostats")
public class ThermostatController {

  private final Map<String, Device> devices;
  private final ApplicationEventPublisher publisher;

  public ThermostatController(Map<String, Device> devices, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.publisher = publisher;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getThermostat(@PathVariable String id) {
    return thermostatInfo((Thermostat) devices.get(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleThermostat(@PathVariable String id) {
    Thermostat t = (Thermostat) devices.get(id);
    t.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/mode")
  public Map<String, Object> setMode(@PathVariable String id, @RequestBody Map<String, String> body) {
    Thermostat t = (Thermostat) devices.get(id);
    String mode = body.get("mode");
    t.setStateMode(ThermostatMode.valueOf(mode));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setMode:" + mode));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/temperature")
  public Map<String, Object> setTemperature(@PathVariable String id, @RequestBody Map<String, Integer> body) {
    Thermostat t = (Thermostat) devices.get(id);
    int temp = body.get("temperature");
    t.setStateDesiredTemperature(temp);
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setTemperature:" + temp));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/multiplier")
  public Map<String, Object> setMultiplier(@PathVariable String id, @RequestBody Map<String, String> body) {
    Thermostat t = (Thermostat) devices.get(id);
    String mult = body.get("multiplier");
    t.setMultiplier(Multiplier.valueOf(mult));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setMultiplier:" + mult));
    return thermostatInfo(t);
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