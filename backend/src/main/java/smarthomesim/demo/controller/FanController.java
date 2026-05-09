package smarthomesim.demo.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import smarthomesim.demo.domain.Device;
import smarthomesim.demo.domain.dto.FanSpeedRequest;
import smarthomesim.demo.domain.events.DeviceStateChangedEvent;
import smarthomesim.demo.domain.exception.DeviceNotFoundException;
import smarthomesim.demo.domain.fan.Fan;
import smarthomesim.demo.domain.fan.Speed;

@RestController
@RequestMapping("/api/fans")
public class FanController {

  private final Map<String, Device> devices;
  private final ApplicationEventPublisher publisher;

  public FanController(Map<String, Device> devices, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.publisher = publisher;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getFan(@PathVariable String id) {
    return fanInfo(requireFan(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleFan(@PathVariable String id) {
    Fan fan = requireFan(id);
    fan.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return fanInfo(fan);
  }

  @PostMapping("/{id}/speed")
  public Map<String, Object> setSpeed(@PathVariable String id, @Valid @RequestBody FanSpeedRequest body) {
    Fan fan = requireFan(id);
    fan.setStateSpeed(Speed.valueOf(body.speed()));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setSpeed:" + body.speed()));
    return fanInfo(fan);
  }

  private Fan requireFan(String id) {
    Device d = devices.get(id);
    if (!(d instanceof Fan f)) throw new DeviceNotFoundException(id);
    return f;
  }

  private Map<String, Object> fanInfo(Fan f) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", f.getId());
    m.put("name", f.getName());
    m.put("location", f.getLocation());
    m.put("type", f.getType());
    m.put("state", f.getFanState());
    m.put("speed", f.getSpeed());
    return m;
  }
}