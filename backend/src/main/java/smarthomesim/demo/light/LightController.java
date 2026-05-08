package smarthomesim.demo.light;
import java.util.HashMap;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smarthomesim.demo.device.Device;
import java.awt.Color;

@RestController
@RequestMapping("/api/lights")

public class LightController {

  private final Map<String, Device> devices;

  // constructor: Spring will inject the device hub
  public LightController(Map<String, Device> devices) {
    this.devices = devices;
  }

  // GET info about a light
  @GetMapping("/{id}")
  public Map<String, Object> getLight(@PathVariable String id) {
    return lightInfo((Light) devices.get(id));
  }

  // POST for toggle
  @PostMapping("/{id}/toggle")
  public Map<String, Object> toggleLight(@PathVariable String id) {
    Light light = (Light) devices.get(id);
    light.togglePower();
    return lightInfo(light);
  }

  // POST for brightness
  @PostMapping("/{id}/brightness")
  public Map<String, Object> setBrightness(@PathVariable String id,
                                           @RequestBody Map<String, Integer> body) {
    Light light = (Light) devices.get(id);
    light.setStateBrightness(body.get("brightness"));
    return lightInfo(light);
  }

  // POST for color
  @PostMapping("/{id}/color")
  public Map<String, Object> setColor(@PathVariable String id,
                                      @RequestBody Map<String, String> body) {
    Light light = (Light) devices.get(id);
    light.setStateColor(Color.decode(body.get("color")));
    return lightInfo(light);
  }

  // helper: shape the JSON the frontend receives
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