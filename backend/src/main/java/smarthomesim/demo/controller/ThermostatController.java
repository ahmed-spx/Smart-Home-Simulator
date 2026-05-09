package smarthomesim.demo.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.dto.ModeRequest;
import smarthomesim.demo.domain.dto.MultiplierRequest;
import smarthomesim.demo.domain.dto.TemperatureRequest;
import smarthomesim.demo.domain.events.DeviceStateChangedEvent;
import smarthomesim.demo.domain.exception.DeviceNotFoundException;
import smarthomesim.demo.domain.thermostat.Multiplier;
import smarthomesim.demo.domain.thermostat.Thermostat;
import smarthomesim.demo.domain.thermostat.ThermostatMode;

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
    return thermostatInfo(requireThermostat(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleThermostat(@PathVariable String id) {
    Thermostat t = requireThermostat(id);
    t.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/mode")
  public Map<String, Object> setMode(@PathVariable String id, @Valid @RequestBody ModeRequest body) {
    Thermostat t = requireThermostat(id);
    t.setStateMode(ThermostatMode.valueOf(body.mode()));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setMode:" + body.mode()));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/temperature")
  public Map<String, Object> setTemperature(@PathVariable String id, @Valid @RequestBody TemperatureRequest body) {
    Thermostat t = requireThermostat(id);
    t.setStateDesiredTemperature(body.temperature());
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setTemperature:" + body.temperature()));
    return thermostatInfo(t);
  }

  @PostMapping("/{id}/multiplier")
  public Map<String, Object> setMultiplier(@PathVariable String id, @Valid @RequestBody MultiplierRequest body) {
    Thermostat t = requireThermostat(id);
    t.setMultiplier(Multiplier.valueOf(body.multiplier()));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setMultiplier:" + body.multiplier()));
    return thermostatInfo(t);
  }

  private Thermostat requireThermostat(String id) {
    Device d = devices.get(id);
    if (!(d instanceof Thermostat t)) throw new DeviceNotFoundException(id);
    return t;
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