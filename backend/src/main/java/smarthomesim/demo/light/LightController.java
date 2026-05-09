package smarthomesim.demo.light;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import smarthomesim.demo.device.Device;
import smarthomesim.demo.dto.BrightnessRequest;
import smarthomesim.demo.dto.ColorRequest;
import smarthomesim.demo.events.DeviceStateChangedEvent;
import smarthomesim.demo.exception.DeviceNotFoundException;

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
    return lightInfo(requireLight(id));
  }

  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleLight(@PathVariable String id) {
    Light light = requireLight(id);
    light.togglePower();
    publisher.publishEvent(new DeviceStateChangedEvent(id, "toggle"));
    return lightInfo(light);
  }

  @PostMapping("/{id}/brightness")
  public Map<String, Object> setBrightness(@PathVariable String id, @Valid @RequestBody BrightnessRequest body) {
    Light light = requireLight(id);
    light.setStateBrightness(body.brightness());
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setBrightness:" + body.brightness()));
    return lightInfo(light);
  }

  @PostMapping("/{id}/color")
  public Map<String, Object> setColor(@PathVariable String id, @Valid @RequestBody ColorRequest body) {
    Light light = requireLight(id);
    light.setStateColor(Color.decode(body.color()));
    publisher.publishEvent(new DeviceStateChangedEvent(id, "setColor:" + body.color()));
    return lightInfo(light);
  }

  private Light requireLight(String id) {
    Device d = devices.get(id);
    if (!(d instanceof Light l)) throw new DeviceNotFoundException(id);
    return l;
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