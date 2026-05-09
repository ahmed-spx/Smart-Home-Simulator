package smarthomesim.demo.light;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.events.DeviceStateChangedEvent;

@RestController
@RequestMapping("/api/lights")
public class LightController {

  private final Map<String, Device> devices;
  private final ApplicationEventPublisher publisher;

  public LightController(Map<String, Device> devices, ApplicationEventPublisher publisher) {
    this.devices = devices;
    this.publisher = publisher;
  }

  @GetMapping("/{id}")
  public Map<String, Object> getLight(@PathVariable String id) {
    return lightInfo((Light) devices.get(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleLight(@PathVariable String id) {
    Light light = (Light) devices.get(id);
    light.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return lightInfo(light);
  }

  @PostMapping("/{id}/brightness")
  public Map<String, Object> setBrightness(@PathVariable String id, @RequestBody Map<String, Integer> body) {
    Light light = (Light) devices.get(id);
    int b = body.get("brightness");
    light.setStateBrightness(b);
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setBrightness:" + b));
    return lightInfo(light);
  }

  @PostMapping("/{id}/color")
  public Map<String, Object> setColor(@PathVariable String id, @RequestBody Map<String, String> body) {
    Light light = (Light) devices.get(id);
    String c = body.get("color");
    light.setStateColor(Color.decode(c));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setColor:" + c));
    return lightInfo(light);
  }

  private Map<String, Object> lightInfo(Light l) {
    Map<String, Object> m = new HashMap<>();
    m.put("id", l.getId());
    m.put("name", l.getName());
    m.put("location", l.getLocation());
    m.put("type", l.getType());
    m.put("state", l.getLightState());
    m.put("brightness", l.getBrightness());
    m.put("color", l.getColorHex());
    return m;
  }
}