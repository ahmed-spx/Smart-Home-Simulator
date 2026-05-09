package smarthomesim.demo.fan;

import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.events.DeviceStateChangedEvent;

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
    return fanInfo((Fan) devices.get(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleFan(@PathVariable String id) {
    Fan fan = (Fan) devices.get(id);
    fan.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return fanInfo(fan);
  }

  @PostMapping("/{id}/speed")
  public Map<String, Object> setSpeed(@PathVariable String id, @RequestBody Map<String, String> body) {
    Fan fan = (Fan) devices.get(id);
    String s = body.get("speed");
    fan.setStateSpeed(Speed.valueOf(s));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setSpeed:" + s));
    return fanInfo(fan);
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